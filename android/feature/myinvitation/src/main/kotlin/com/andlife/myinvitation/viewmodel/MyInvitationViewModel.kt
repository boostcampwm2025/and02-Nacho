package com.andlife.myinvitation.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.andlife.domain.model.invitation.InvitationStatus
import com.andlife.domain.model.invitation.SortDirection
import com.andlife.domain.repository.auth.AuthStateManager
import com.andlife.domain.repository.invitation.InvitationRepository
import com.andlife.domain.util.AnalyticsEvent
import com.andlife.domain.util.AnalyticsLogger
import com.andlife.domain.util.Button
import com.andlife.domain.util.Screen
import com.andlife.domain.util.onFailure
import com.andlife.domain.util.onSuccess
import com.andlife.model.invitation.InvitationSummaryUiModel
import com.andlife.model.invitation.toUiModel
import com.andlife.myinvitation.model.MyInvitationSideEffect
import com.andlife.myinvitation.model.MyInvitationSideEffect.*
import com.andlife.myinvitation.model.MyInvitationUiEvent
import com.andlife.myinvitation.model.MyInvitationUiState
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
class MyInvitationViewModel @Inject constructor(
    private val invitationRepository: InvitationRepository,
    private val authStateManager: AuthStateManager,
    private val analyticsLogger: AnalyticsLogger
) : BaseViewModel<MyInvitationUiState, MyInvitationUiEvent, MyInvitationSideEffect>(
    initialState = MyInvitationUiState()
) {
    private val _upcomingSort = MutableStateFlow(SortDirection.ASC)
    private val _pastSort = MutableStateFlow(SortDirection.DESC)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val upcomingMyInvitationPagingFlow: Flow<PagingData<InvitationSummaryUiModel>> =
        combine(_upcomingSort, authStateManager.authState) { sort, _ -> sort }
            .flatMapLatest { sort ->
                invitationRepository.getMyInvitations(
                    status = InvitationStatus.UPCOMING,
                    sortType = sort,
                    isMyInvitation = true,
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
    val pastMyInvitationPagingFlow: Flow<PagingData<InvitationSummaryUiModel>> =
        combine(_pastSort, authStateManager.authState) { sort, _ -> sort }
            .flatMapLatest { sort ->
                invitationRepository.getMyInvitations(
                    status = InvitationStatus.PAST,
                    sortType = sort,
                    isMyInvitation = true,
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

    val authState = authStateManager.authState

    override val uiState: StateFlow<MyInvitationUiState> =
        mutableUiState
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = MyInvitationUiState()
            )

    override fun onEvent(event: MyInvitationUiEvent) {
        when (event) {
            is MyInvitationUiEvent.SelectTab -> {
                updateState { copy(selectedTab = event.index) }
            }
            is MyInvitationUiEvent.ClickInvitation -> {
                sendEffect(NavigateToDetail(event.id))
            }
            is MyInvitationUiEvent.ChangeSort -> {
                if (event.isUpcoming) {
                    analyticsLogger.logEvent(AnalyticsEvent.ButtonClick(Screen.MY_INVITATION, Button.MY_INVITATION_SORT_UPCOMING))
                    _upcomingSort.value = event.newSort
                } else {
                    analyticsLogger.logEvent(AnalyticsEvent.ButtonClick(Screen.MY_INVITATION, Button.MY_INVITATION_SORT_PAST))
                    _pastSort.value = event.newSort
                }
            }
            is MyInvitationUiEvent.ClickCreate -> {
                sendEffect(MyInvitationSideEffect.NavigateToCreate)
            }
            is MyInvitationUiEvent.ClickDeleteInvitation -> {
                analyticsLogger.logEvent(AnalyticsEvent.ButtonClick(Screen.MY_INVITATION, Button.DELETE_INVITATION))
                deleteInvitation(event.id)
            }

            MyInvitationUiEvent.ClickLogin -> {
                sendEffect(NavigateToLogin)
            }
        }
    }

    fun deleteInvitation(invitationId: Long) {
        viewModelScope.launch {
            invitationRepository.deleteInvitation(invitationId)
                .onSuccess {
                    sendEffect(MyInvitationSideEffect.DeleteSuccess)
                }
                .onFailure { it, msg ->
                    sendEffect(MyInvitationSideEffect.DeleteFailure)
                }
        }
    }

    fun handleRefresh() {
        sendEffect(MyInvitationSideEffect.NeedRefresh)
    }
}
