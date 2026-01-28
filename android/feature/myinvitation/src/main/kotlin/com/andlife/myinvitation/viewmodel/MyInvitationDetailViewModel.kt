package com.andlife.myinvitation.viewmodel

import android.text.Editable
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.andlife.domain.repository.invitation.InvitationRepository
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
                        hasThanksCard = false, // TODO: 감사카드 존재 여부는 별도 API로 확인 필요
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

    private fun deleteInvitation() { /* TODO: 초대장 삭제 로직 */
    }

    private fun navigateToEditInvitation() {
        sendEffect(MyInvitationDetailSideEffect.NavigateToEditInvitation(myInvitationId))
    }

    private fun showThanksCardOnboarding() { /* TODO: 감사카드 온보딩 */
    }

    private fun navigateToCreateThanksCard() { /* TODO: 감사카드 작성 이동 */
    }

    private fun navigateToEditCard() { // TODO: 초대카드 편집 이동
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

    private fun navigateToFullScreenImage(imageList: ImmutableList<String>, index: Int) { /* TODO: 이미지 풀스크린*/ }

    private fun showMapErrorSnackbar() {
        sendEffect(MyInvitationDetailSideEffect.ShowMapErrorSnackbar)
    }

    private fun retryLoad() {
        viewModelScope.launch {
            loadInvitation()
            updateState { copy(editCardEnabled = false, cachedCardEditable = null) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        createCardSession.clear()
    }
}
