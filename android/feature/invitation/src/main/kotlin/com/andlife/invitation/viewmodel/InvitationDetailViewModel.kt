package com.andlife.invitation.viewmodel

import android.text.Editable
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.andlife.domain.repository.invitation.InvitationRepository
import com.andlife.domain.util.AnalyticsEvent
import com.andlife.domain.util.AnalyticsLogger
import com.andlife.domain.util.Button
import com.andlife.domain.util.onFailure
import com.andlife.domain.util.onSuccess
import com.andlife.invitation.InvitationDetail
import com.andlife.invitation.model.detail.InvitationDetailSideEffect
import com.andlife.invitation.model.detail.InvitationDetailUiEvent
import com.andlife.invitation.model.detail.InvitationDetailUiState
import com.andlife.model.invitation.toContentsUiModel
import com.andlife.domain.util.RefreshEventHub
import com.andlife.domain.util.RefreshEventHub.RefreshTarget
import com.andlife.domain.util.Screen
import com.andlife.ui.base.BaseViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel(assistedFactory = InvitationDetailViewModel.Factory::class)
class InvitationDetailViewModel @AssistedInject constructor(
    savedStateHandle: SavedStateHandle,
    private val invitationRepository: InvitationRepository,
    private val analyticsLogger: AnalyticsLogger,
    @Assisted val invitationId: Long,
    @Assisted val isFromDeepLink: Boolean,
) : BaseViewModel<InvitationDetailUiState, InvitationDetailUiEvent, InvitationDetailSideEffect>(
    initialState = InvitationDetailUiState(),
) {

    override val uiState: StateFlow<InvitationDetailUiState> =
        mutableUiState
            .onStart {
                Log.d(
                    "InvitationDetailViewModel",
                    "[${this@InvitationDetailViewModel.hashCode()}] 딥링크 진입 : $isFromDeepLink"
                )
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
            .onSuccess { joinResult ->
                if (!joinResult.isMember) {
                    RefreshEventHub.emit(RefreshTarget.INVITATION)
                }
                loadInvitation()
            }
            .onFailure { error, _ ->
                updateState { copy(isLoading = false, isError = true) }
            }
    }

    private suspend fun loadInvitation() {
        Log.d("InvitationDetailViewModel", "id: $invitationId")
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
            is InvitationDetailUiEvent.ClickThanksCard -> {
                analyticsLogger.logEvent(
                    AnalyticsEvent.ButtonClick(
                        Screen.INVITATION_DETAIL,
                        Button.SHOW_THANKS_CARD
                    )
                )
                showThanksCardOnboarding()
            }

            is InvitationDetailUiEvent.ClickLeaveInvitation -> {
                analyticsLogger.logEvent(
                    AnalyticsEvent.ButtonClick(
                        Screen.INVITATION_DETAIL,
                        Button.INVITATION_LEAVE
                    )
                )
                leaveInvitation()
            }

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

    private fun navigateToFullScreenImage(imageList: ImmutableList<String>, index: Int) { /* TODO: 이미지 풀스크린*/
    }

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
    @AssistedFactory
    interface Factory {
        fun create(
            invitationId: Long,
            isFromDeepLink: Boolean
        ): InvitationDetailViewModel
    }
}
