package com.andlife.myinvitation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.andlife.myinvitation.manager.KakaoShareManager
import com.andlife.myinvitation.model.MyInvitationDetailSideEffect
import com.andlife.myinvitation.model.MyInvitationDetailUiEvent
import com.andlife.myinvitation.model.MyInvitationDetailUiState
import com.andlife.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
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
) : BaseViewModel<MyInvitationDetailUiState, MyInvitationDetailUiEvent, MyInvitationDetailSideEffect>(
    initialState = MyInvitationDetailUiState(),
) {
    private val myInvitationId: Long? = savedStateHandle[KEY_INVITE_ID]

    override val uiState: StateFlow<MyInvitationDetailUiState> =
        mutableUiState
            .onStart {
                loadInvitationDetail()
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = MyInvitationDetailUiState(),
            )

    private fun loadInvitationDetail() {
        if(myInvitationId == null) {
            return
        }

        viewModelScope.launch {
            // TODO: Repository 호출
            updateState { copy(id = myInvitationId) }
        }
    }

    override fun onEvent(event: MyInvitationDetailUiEvent) {
        when (event) {
            is MyInvitationDetailUiEvent.ClickBack -> clickClose()
            is MyInvitationDetailUiEvent.ClickShare -> shareInvitation()
        }
    }

    private fun clickClose() {
        sendEffect(MyInvitationDetailSideEffect.NavigateBack)
    }

    private fun shareInvitation() {
        val currentId = uiState.value.id

        kakaoShareManager.share(
            invitationId = currentId,
        )
    }

    companion object {
        private const val KEY_INVITE_ID = "invite_id"
    }
}
