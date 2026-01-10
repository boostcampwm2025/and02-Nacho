package com.andlife.invitation.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.andlife.invitation.model.InvitationDetailSideEffect
import com.andlife.invitation.model.InvitationDetailUiEvent
import com.andlife.invitation.model.InvitationDetailUiState
import com.andlife.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvitationDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<InvitationDetailUiState, InvitationDetailUiEvent, InvitationDetailSideEffect>(
    initialState = InvitationDetailUiState(),
) {
    private val invitationId: Long? = savedStateHandle["id"]

    override val uiState: StateFlow<InvitationDetailUiState> = mutableUiState
        .onStart {
            loadInvitationDetail()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = InvitationDetailUiState()
        )

    private fun loadInvitationDetail() {
        if (invitationId == null) {
            return
        }

        viewModelScope.launch {
            // TODO: 초대장 상세 정보 로드 구현 필요
            // - Repository를 통해 초대장 정보 조회 (invitationId 사용)
            // - API 호출 후 uiState 업데이트
            Log.d("DeepLink", "전달받은 ID: $invitationId")
            updateState { copy(id = invitationId) }
        }
    }

    override fun onEvent(event: InvitationDetailUiEvent) {
        when (event) {
            is InvitationDetailUiEvent.ClickBack -> clickClose()
        }
    }

    private fun clickClose() {
        sendEffect(InvitationDetailSideEffect.NavigateBack)
    }
}
