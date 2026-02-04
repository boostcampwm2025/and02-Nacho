package com.andlife.invitation.viewmodel

import android.text.Editable
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
import com.andlife.domain.util.RefreshEventHub
import com.andlife.domain.util.RefreshEventHub.RefreshTarget
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
    private val route = savedStateHandle.toRoute<InvitationDetail>()
    private val isFromDeepLink: Boolean = route.isFromDeepLink
    private val invitationId: Long = route.id

    override val uiState: StateFlow<InvitationDetailUiState> =
        mutableUiState
            .onStart {
                Log.d("DeepLink Debug", "[${this@InvitationDetailViewModel.hashCode()}] 딥링크 진입 : $isFromDeepLink")
                if (isFromDeepLink) {
                    joinAndLoadInvitation()
                } else {
                    loadInvitation()
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = InvitationDetailUiState()
            )

    private suspend fun joinAndLoadInvitation() {
        updateState { copy(isLoading = true, isError = false) }

        invitationRepository.joinInvitation(invitationId)
            .onSuccess {
                RefreshEventHub.emit(RefreshTarget.HOME)
                RefreshEventHub.emit(RefreshTarget.INVITATION)
                loadInvitation()
            }
            .onFailure { error, _ ->
                updateState { copy(isLoading = false, isError = true) }
            }
    }

    private suspend fun loadInvitation() {
        updateState { copy(isLoading = true, isError = false, editableCache = null) }

        invitationRepository.getInvitation(invitationId)
            .onSuccess { invitation ->
                updateState {
                    copy(
                        isLoading = false,
                        isError = false,
                        invitationContentsUiModel = invitation.toContentsUiModel(),
                    )
                }
            }.onFailure { it, msg ->
                updateState { copy(isLoading = false, isError = true) }
            }
    }

    override fun onEvent(event: InvitationDetailUiEvent) {
        when (event) {
            is InvitationDetailUiEvent.ClickBack -> clickClose()
            is InvitationDetailUiEvent.ClickThanksCard -> showThanksCardOnboarding()
            is InvitationDetailUiEvent.ClickLeaveInvitation -> leaveInvitation()
            is InvitationDetailUiEvent.ClickImage -> navigateToFullScreenImage(event.imageList, event.index)
            is InvitationDetailUiEvent.MapError -> showMapErrorSnackbar()
            is InvitationDetailUiEvent.RetryLoad -> retryLoad()
            InvitationDetailUiEvent.LottieStarted -> updateLottieStarted()
        }
    }

    private fun clickClose() {
        sendEffect(InvitationDetailSideEffect.NavigateBack)
    }

    private fun leaveInvitation() {
        viewModelScope.launch {
            updateState { copy(isOverlayLoading = true) }
            invitationRepository.leaveInvitation(invitationId)
                .onSuccess {
                    sendEffect(InvitationDetailSideEffect.NavigateBack)
                    RefreshEventHub.emit(RefreshTarget.INVITATION)
                    RefreshEventHub.emit(RefreshTarget.HOME)
                }
                .onFailure { error, _ ->
                    sendEffect(InvitationDetailSideEffect.ShowLeaveInvitationErrorSnackbar)
                }
            updateState { copy(isOverlayLoading = false) }
        }
    }

    private fun showThanksCardOnboarding() {
        sendEffect(InvitationDetailSideEffect.ThanksCardOnBoarding)
    }

    private fun navigateToFullScreenImage(imageList: ImmutableList<String>, index: Int) { /* TODO: 이미지 풀스크린*/ }

    private fun showMapErrorSnackbar() {
        sendEffect(InvitationDetailSideEffect.ShowMapErrorSnackbar)
    }

    fun saveEditable(editable: Editable) {
        updateState { copy(editableCache = editable) }
    }

    fun saveThanksCardEditableCache(editable: Editable) {
        updateState { copy(thanksCardEditableCache = editable) }
    }

    private fun retryLoad() {
        viewModelScope.launch {
            loadInvitation()
            updateState { copy(hasShownLottie = false) }
        }
    }

    private fun updateLottieStarted() {
        updateState { copy(hasShownLottie = true) }
    }

}
