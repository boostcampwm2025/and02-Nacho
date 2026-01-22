package com.andlife.invitation.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.andlife.domain.repository.invitation.InvitationRepository
import com.andlife.domain.util.onFailure
import com.andlife.domain.util.onSuccess
import com.andlife.invitation.InvitationDetail
import com.andlife.invitation.model.detail.InvitationDetailSideEffect
import com.andlife.invitation.model.detail.InvitationDetailUiEvent
import com.andlife.invitation.model.detail.InvitationDetailUiState
import com.andlife.model.invitation.toContentsUiModel
import com.andlife.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvitationDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val invitationRepository: InvitationRepository,
) : BaseViewModel<InvitationDetailUiState, InvitationDetailUiEvent, InvitationDetailSideEffect>(
    initialState = InvitationDetailUiState(),
) {
    private val invitationId: Long = savedStateHandle.toRoute<InvitationDetail>().id

    override val uiState: StateFlow<InvitationDetailUiState> =
        mutableUiState
            .onStart {
                loadInvitation()
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = InvitationDetailUiState()
            )

    private suspend fun loadInvitation() {
        updateState { copy(isLoading = true, isError = false) }

        invitationRepository.getInvitation(invitationId)
            .onSuccess { invitation ->
                updateState {
                    copy(
                        isLoading = false,
                        isError = false,
                        hasThanksCard = false, // TODO: 감사카드 존재 여부는 별도 API로 확인 필요
                        invitationContentsUiModel = invitation.toContentsUiModel(),
                    )
                }
            }.onFailure { it, msg ->
                updateState { copy(isLoading = false, isError = true) }
                Log.e("InvitationDetailViewModel", "에러 발생: $it")
            }
    }

    override fun onEvent(event: InvitationDetailUiEvent) {
        when (event) {
            is InvitationDetailUiEvent.ClickBack -> clickClose()
            is InvitationDetailUiEvent.ClickThanksCard -> showThanksCardOnboarding()
            is InvitationDetailUiEvent.ClickDelete -> deleteInvitation()
            is InvitationDetailUiEvent.ClickImage -> navigateToFullScreenImage(event.imageList, event.index)
            is InvitationDetailUiEvent.MapError -> showMapErrorSnackbar()
            is InvitationDetailUiEvent.RetryLoad -> retryLoad()
        }
    }

    private fun clickClose() {
        sendEffect(InvitationDetailSideEffect.NavigateBack)
    }

    private fun deleteInvitation() { /* TODO: 초대장 삭제 로직 */ }

    private fun showThanksCardOnboarding() { /* TODO: 감사카드 온보딩 */ }

    private fun navigateToFullScreenImage(imageList: ImmutableList<String>, index: Int) { /* TODO: 이미지 풀스크린*/ }

    private fun showMapErrorSnackbar() {
        sendEffect(InvitationDetailSideEffect.ShowMapErrorSnackbar)
    }

    private fun retryLoad() {
        viewModelScope.launch {
            loadInvitation()
        }
    }
}
