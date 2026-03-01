package com.andlife.invitation.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.andlife.domain.error.DataError
import com.andlife.domain.model.auth.AuthState
import com.andlife.domain.model.invitation.InvitationStatus
import com.andlife.domain.model.invitation.SortDirection
import com.andlife.domain.repository.auth.AuthStateManager
import com.andlife.domain.repository.invitation.InvitationRepository
import com.andlife.domain.repository.report.ReportRepository
import com.andlife.domain.util.AnalyticsEvent
import com.andlife.domain.util.AnalyticsLogger
import com.andlife.domain.util.Button
import com.andlife.domain.util.CrashlyticsLogger
import com.andlife.domain.util.EventType
import com.andlife.domain.util.RefreshEventHub
import com.andlife.domain.util.RefreshEventHub.RefreshTarget
import com.andlife.domain.util.Screen
import com.andlife.domain.util.onFailure
import com.andlife.domain.util.onSuccess
import com.andlife.invitation.model.InvitationSideEffect
import com.andlife.invitation.model.InvitationUiEvent
import com.andlife.invitation.model.InvitationUiState
import com.andlife.model.common.ReportReason
import com.andlife.model.common.ReportTargetType
import com.andlife.model.invitation.InvitationSummaryUiModel
import com.andlife.model.invitation.toUiModel
import com.andlife.ui.base.BaseViewModel
import com.andlife.ui.util.toFullDisplayString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class InvitationViewModel @Inject constructor(
    private val invitationRepository: InvitationRepository,
    private val reportRepository: ReportRepository,
    private val authStateManager: AuthStateManager,
    private val analyticsLogger: AnalyticsLogger,
    private val crashlyticsLogger: CrashlyticsLogger,
) : BaseViewModel<InvitationUiState, InvitationUiEvent, InvitationSideEffect>(
    initialState = InvitationUiState()
) {
    private val _upcomingSort = MutableStateFlow(SortDirection.ASC)
    private val _pastSort = MutableStateFlow(SortDirection.DESC)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val upcomingInvitationPagingFlow: Flow<PagingData<InvitationSummaryUiModel>> =
        combine(_upcomingSort, authStateManager.authState) { sort, _ -> sort }
            .flatMapLatest { sort ->
                invitationRepository.getParticipantInvitations(
                    status = InvitationStatus.UPCOMING,
                    sortType = sort,
                    isMyInvitation = false,
                    onTotalCountLoaded = { totalCount ->
                        updateState { copy(upcomingTotalCount = totalCount) }
                    }
                ).map { pagingData ->
                    pagingData.map { summary ->
                        summary.toUiModel { date, time ->
                            LocalDateTime(date, time).toFullDisplayString()
                        }
                    }
                }
            }.cachedIn(viewModelScope)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val pastInvitationPagingFlow: Flow<PagingData<InvitationSummaryUiModel>> =
        combine(_pastSort, authStateManager.authState) { sort, _ -> sort }
            .flatMapLatest { sort ->
                invitationRepository.getParticipantInvitations(
                    status = InvitationStatus.PAST,
                    sortType = sort,
                    isMyInvitation = false,
                    onTotalCountLoaded = { totalCount ->
                        updateState { copy(pastTotalCount = totalCount) }
                    }
                ).map { pagingData ->
                    pagingData.map { summary ->
                        summary.toUiModel { date, time ->
                            LocalDateTime(date, time).toFullDisplayString()
                        }
                    }
                }
            }.cachedIn(viewModelScope)

    override val uiState: StateFlow<InvitationUiState> =
        mutableUiState
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = InvitationUiState()
            )

    override fun onEvent(event: InvitationUiEvent) {
        when (event) {
            is InvitationUiEvent.SelectTab -> {
                updateState { copy(selectedTab = event.index) }
            }

            is InvitationUiEvent.ClickInvitation -> {
                sendEffect(InvitationSideEffect.NavigateToDetail(event.id))
            }

            is InvitationUiEvent.ChangeSort -> {
                if (event.isUpcoming) {
                    analyticsLogger.logEvent(
                        AnalyticsEvent.ButtonClick(
                            Screen.INVITATION,
                            Button.INVITATION_SORT_UPCOMING
                        )
                    )
                    _upcomingSort.value = event.newSort
                } else {
                    analyticsLogger.logEvent(AnalyticsEvent.ButtonClick(Screen.INVITATION, Button.INVITATION_SORT_PAST))
                    _pastSort.value = event.newSort
                }
            }

            is InvitationUiEvent.ClickLeaveInvitation -> {
                analyticsLogger.logEvent(AnalyticsEvent.ButtonClick(Screen.INVITATION, Button.INVITATION_LEAVE))
                leaveInvitation(event.id)
            }

            is InvitationUiEvent.ShowReport -> {
                val authState = authStateManager.authState.value
                if (authState !is AuthState.Authenticated) {
                    //sendEffect(InvitationSideEffect.NavigateToLogin)
                    updateState { copy(showLoginDialog = true) }
                } else {
                    updateState { copy(reportTargetId = event.invitationId) }
                }
            }

            is InvitationUiEvent.DismissReport -> {
                updateState { copy(reportTargetId = null) }
            }

            is InvitationUiEvent.SubmitReport -> {
                analyticsLogger.logEvent(AnalyticsEvent.ButtonClick(Screen.INVITATION, Button.INVITATION_REPORT))
                submitReport(event.reason, event.description)
            }

            is InvitationUiEvent.DismissLoginDialog -> {
                updateState { copy(showLoginDialog = false) }
            }
        }
    }

    fun leaveInvitation(invitationId: Long) {
        viewModelScope.launch {
            invitationRepository.leaveInvitation(invitationId)
                .onSuccess {
                    sendEffect(InvitationSideEffect.LeaveSuccess)
                    RefreshEventHub.emit(RefreshTarget.HOME)
                }
                .onFailure { it, msg ->
                    sendEffect(InvitationSideEffect.LeaveFailure)
                    Log.e("InvitationViewModel", "에러 발생: $it")
                }
        }
    }

    fun handleRefresh() {
        sendEffect(InvitationSideEffect.NeedRefresh)
    }

    private fun submitReport(reason: ReportReason, description: String?) {
        val targetId = mutableUiState.value.reportTargetId ?: return
        viewModelScope.launch {
            reportRepository.sendReport(
                targetType = ReportTargetType.INVITATION.name,
                targetId = targetId,
                reason = reason.name,
                description = description
            ).onSuccess {
                analyticsLogger.logEvent(AnalyticsEvent.Event(EventType.SUCCESS_REPORT_INVITATION.value))
                updateState { copy(reportTargetId = null) }
                sendEffect(InvitationSideEffect.ReportSuccess)
            }.onFailure { error, message ->
                analyticsLogger.logEvent(
                    AnalyticsEvent.Event(
                        EventType.FAIL_REPORT_INVITATION.value, mapOf(
                            "id" to targetId.toString(),
                            "error" to "$error: $message"
                        )
                    )
                )
                crashlyticsLogger.recordException("$targetId: $error - $message")
                val messageToShow = if (error == DataError.Network.CONFLICT) {
                    message
                } else {
                    null
                }
                sendEffect(InvitationSideEffect.ReportFailure(messageToShow))
            }
        }
    }
}
