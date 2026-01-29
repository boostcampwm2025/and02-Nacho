package com.andlife.invitation.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.andlife.domain.model.invitation.InvitationStatus
import com.andlife.domain.model.invitation.SortDirection
import com.andlife.domain.repository.invitation.InvitationRepository
import com.andlife.domain.util.onFailure
import com.andlife.domain.util.onSuccess
import com.andlife.invitation.model.InvitationSideEffect
import com.andlife.invitation.model.InvitationUiEvent
import com.andlife.invitation.model.InvitationUiState
import com.andlife.model.invitation.InvitationSummaryUiModel
import com.andlife.model.invitation.toUiModel
import com.andlife.ui.base.BaseViewModel
import com.andlife.ui.util.toFullDisplayString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class InvitationViewModel @Inject constructor(
    private val invitationRepository: InvitationRepository
) : BaseViewModel<InvitationUiState, InvitationUiEvent, InvitationSideEffect>(
    initialState = InvitationUiState()
) {
    private val _upcomingSort = MutableStateFlow(SortDirection.ASC)
    private val _pastSort = MutableStateFlow(SortDirection.DESC)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val upcomingInvitationPagingFlow: Flow<PagingData<InvitationSummaryUiModel>> =
        _upcomingSort.flatMapLatest { sort ->
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
        _pastSort.flatMapLatest { sort ->
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
            is InvitationUiEvent.Refresh -> {
                updateState { copy(isRefreshing = true) }
            }

            is InvitationUiEvent.SelectTab -> {
                updateState { copy(selectedTab = event.index) }
            }

            is InvitationUiEvent.ClickInvitation -> {
                sendEffect(InvitationSideEffect.NavigateToDetail(event.id))
            }
            is InvitationUiEvent.ChangeSort -> {
                if (event.isUpcoming) {
                    _upcomingSort.value = event.newSort
                } else {
                    _pastSort.value = event.newSort
                }
            }
            is InvitationUiEvent.ClickLeaveInvitation -> {
                leaveInvitation(event.id)
            }
        }
    }

    fun leaveInvitation(invitationId: Long) {
        viewModelScope.launch {
            updateState { copy(isRefreshing = true) }
            invitationRepository.leaveInvitation(invitationId)
                .onSuccess {
                    sendEffect(InvitationSideEffect.LeaveSuccess)
                }
                .onFailure { it, msg ->
                    sendEffect(InvitationSideEffect.LeaveFailure)
                    Log.e("InvitationViewModel", "에러 발생: $it")
                }
            updateState { copy(isRefreshing = false) }
        }
    }

    fun onRefreshFinished(hasError: Boolean) {
        updateState { copy(isRefreshing = false) }
        if (hasError) sendEffect(InvitationSideEffect.RefreshFailure)
    }

    fun handleDeepLinkRefresh() {
        sendEffect(InvitationSideEffect.RefreshFromDeepLink)
    }

}
