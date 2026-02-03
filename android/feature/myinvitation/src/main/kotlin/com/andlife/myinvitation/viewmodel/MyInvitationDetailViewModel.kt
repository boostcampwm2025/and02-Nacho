package com.andlife.myinvitation.viewmodel

import android.content.ClipData
import android.content.ClipboardManager
import android.text.Editable
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.andlife.deeplink.DeepLinkManager
import com.andlife.domain.repository.invitation.InvitationRepository
import com.andlife.domain.repository.thankscard.ThanksCardRepository
import com.andlife.domain.util.onFailure
import com.andlife.domain.util.onSuccess
import com.andlife.editor.util.CreateCardSession
import com.andlife.model.invitation.toContentsUiModel
import com.andlife.myinvitation.MyInvitationDetail
import com.andlife.myinvitation.manager.KakaoShareManager
import com.andlife.myinvitation.model.detail.MyInvitationDetailSideEffect
import com.andlife.myinvitation.model.detail.MyInvitationDetailUiEvent
import com.andlife.myinvitation.model.detail.MyInvitationDetailUiState
import com.andlife.ui.base.BaseViewModel
import com.andlife.ui.util.toDateTimeSingleLine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyInvitationDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val kakaoShareManager: KakaoShareManager,
    private val invitationRepository: InvitationRepository,
    private val createCardSession: CreateCardSession,
    private val deepLinkManager: DeepLinkManager,
    private val clipboardManager: ClipboardManager,
    private val thanksCardRepository: ThanksCardRepository,
) : BaseViewModel<MyInvitationDetailUiState, MyInvitationDetailUiEvent, MyInvitationDetailSideEffect>(
    initialState = MyInvitationDetailUiState(),
) {

    private val myInvitationId: Long = savedStateHandle.toRoute<MyInvitationDetail>().id

    override val uiState: StateFlow<MyInvitationDetailUiState> =
        mutableUiState
            .onStart {
                loadInvitation()
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = MyInvitationDetailUiState(),
            )

    private suspend fun loadInvitation() {
        updateState { copy(isLoading = true, isError = false) }

        invitationRepository.getInvitation(myInvitationId)
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
                Log.e("MyInvitationDetailViewModel", "에러 발생: $it")
            }
    }

    override fun onEvent(event: MyInvitationDetailUiEvent) {
        when (event) {
            is MyInvitationDetailUiEvent.ClickBack -> clickClose()
            is MyInvitationDetailUiEvent.ClickThanksCard -> showThanksCardOnboarding()
            is MyInvitationDetailUiEvent.ClickShare -> shareInvitation()
            is MyInvitationDetailUiEvent.ClickEdit -> navigateToEditInvitation()
            is MyInvitationDetailUiEvent.ClickDelete -> deleteInvitation()
            is MyInvitationDetailUiEvent.CreateThanksCard -> navigateToCreateThanksCard()
            is MyInvitationDetailUiEvent.ClickEditCard -> navigateToEditCard()
            is MyInvitationDetailUiEvent.ClickImage -> navigateToFullScreenImage(event.imageList, event.index)
            is MyInvitationDetailUiEvent.MapError -> showMapErrorSnackbar()
            is MyInvitationDetailUiEvent.RetryLoad -> retryLoad()
            MyInvitationDetailUiEvent.ClickCreateCard -> navigateToCreateCard()
            MyInvitationDetailUiEvent.CopyInvitationLink -> copyInvitationLink()
            MyInvitationDetailUiEvent.ClickDeleteThanksCard -> deleteThanksCard()
            is MyInvitationDetailUiEvent.ClickUpdateThanksCard -> updateThanksCard(event.cardId)
            MyInvitationDetailUiEvent.LottieStarted -> updateLottieStarted()
        }
    }

    private fun clickClose() {
        sendEffect(MyInvitationDetailSideEffect.NavigateBack)
    }

    private fun shareInvitation() {
        val state = uiState.value
        val content = state.invitationContentsUiModel
        val dateText = content.dateTime.toDateTimeSingleLine()
        val locationText = content.location.name
        val firstImage = content.imageList.firstOrNull()

        kakaoShareManager.share(
            invitationId = myInvitationId,
            title = content.title,
            imageUrl = firstImage,
            date = dateText,
            location = locationText,
        )
    }

    private fun deleteInvitation() {
    }

    private fun navigateToEditInvitation() {
        sendEffect(MyInvitationDetailSideEffect.NavigateToEditInvitation(myInvitationId))
    }

    private fun showThanksCardOnboarding() {
        sendEffect(MyInvitationDetailSideEffect.ThanksCardOnBoarding)
    }

    private fun navigateToCreateThanksCard() {
        sendEffect(MyInvitationDetailSideEffect.NavigateToCreateThanksCard(myInvitationId))
    }

    private fun navigateToEditCard() {
        val card = uiState.value.invitationContentsUiModel.invitationCard
        if (card == null) return
        val cardId = card.card.id ?: return
        sendEffect(MyInvitationDetailSideEffect.NavigateToEditCard(cardId))
    }

    private fun navigateToCreateCard() {
        sendEffect(MyInvitationDetailSideEffect.NavigateToCreateCard(myInvitationId))
    }

    fun saveEditableCache(editable: Editable) {
        val cardUiModel = uiState.value.invitationContentsUiModel.invitationCard
        if (cardUiModel == null) return
        createCardSession.save(editable, Color(cardUiModel.card.backgroundColor), cardUiModel.card.backgroundImageUrl)
        updateState { copy(editCardEnabled = true, cachedCardEditable = editable) }
    }

    fun saveThanksCardEditableCache(editable: Editable) {
        updateState { copy(thanksCardEditableCache = editable) }
    }

    private fun navigateToFullScreenImage(imageList: ImmutableList<String>, index: Int) { /* TODO: 이미지 풀스크린*/ }

    private fun copyInvitationLink() {
        val url = deepLinkManager.buildAppsFlyerUrl(myInvitationId)
        val clip = ClipData.newPlainText(CLIP_LABEL_INVITATION, url)
        clipboardManager.setPrimaryClip(clip)
        sendEffect(MyInvitationDetailSideEffect.LinkCopied)
    }

    private fun showMapErrorSnackbar() {
        sendEffect(MyInvitationDetailSideEffect.ShowMapErrorSnackbar)
    }

    private fun updateThanksCard(cardId: Long) {
        sendEffect(MyInvitationDetailSideEffect.NavigateToUpdateThanksCard(cardId))
    }

    private fun deleteThanksCard() {
        viewModelScope.launch {
            uiState.value.invitationContentsUiModel.thanksCard ?: return@launch
            updateState { copy(isOverlayLoading = true) }
            thanksCardRepository.deleteThanksCard(myInvitationId)
                .onSuccess {
                    updateState { copy(invitationContentsUiModel = invitationContentsUiModel.copy(thanksCard = null)) }
                    sendEffect(MyInvitationDetailSideEffect.SuccessRemoveThanksCard)
                }
                .onFailure { error, msg ->
                    sendEffect(MyInvitationDetailSideEffect.FailRemoveThanksCard)
                }
            updateState { copy(isOverlayLoading = false) }
        }
    }

    private fun retryLoad() {
        viewModelScope.launch {
            loadInvitation()
            updateState { copy(editCardEnabled = false, cachedCardEditable = null, thanksCardEditableCache = null) }
        }
    }

    private fun updateLottieStarted() {
        updateState { copy(hasShownLottie = true) }
    }

    override fun onCleared() {
        super.onCleared()
        createCardSession.clear()
    }

    companion object {
        private const val CLIP_LABEL_INVITATION = "invitation_link"
    }
}
