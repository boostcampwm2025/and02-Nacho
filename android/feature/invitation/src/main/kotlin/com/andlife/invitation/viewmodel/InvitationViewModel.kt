package com.andlife.invitation.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.andlife.domain.repository.invitation.InvitationRepository
import com.andlife.invitation.model.InvitationSideEffect
import com.andlife.invitation.model.InvitationUiEvent
import com.andlife.invitation.model.InvitationUiState
import com.andlife.model.invitation.InvitationSummaryUiModel
import com.andlife.model.invitation.toUiModel
import com.andlife.ui.base.BaseViewModel
import com.andlife.ui.util.toFullDisplayString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class InvitationViewModel @Inject constructor(
    private val invitationRepository: InvitationRepository
) : BaseViewModel<InvitationUiState, InvitationUiEvent, InvitationSideEffect>(
    initialState = InvitationUiState()
) {
    val upcomingInvitationPagingFlow: Flow<PagingData<InvitationSummaryUiModel>> =
        invitationRepository.getParticipantInvitations(status = "UPCOMING")
            .map { pagingData ->
                pagingData.map { summary ->
                    summary.toUiModel { date, time ->
                        LocalDateTime(date, time).toFullDisplayString()
                    }
                }
            }
            .cachedIn(viewModelScope)

    val pastInvitationPagingFlow: Flow<PagingData<InvitationSummaryUiModel>> =
        invitationRepository.getParticipantInvitations(status = "PAST")
            .map { pagingData ->
                pagingData.map { summary ->
                    summary.toUiModel { date, time ->
                        LocalDateTime(date, time).toFullDisplayString()
                    }
                }
            }
            .cachedIn(viewModelScope)

    override val uiState: StateFlow<InvitationUiState> =
        mutableUiState
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = InvitationUiState()
            )

    override fun onEvent(event: InvitationUiEvent) {
        TODO()
    }

}
