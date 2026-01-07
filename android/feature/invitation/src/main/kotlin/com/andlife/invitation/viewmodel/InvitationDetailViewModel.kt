package com.andlife.invitation.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.andlife.invitation.model.InvitationDetailSideEffect
import com.andlife.invitation.model.InvitationDetailUiEvent
import com.andlife.invitation.model.InvitationDetailUiState
import com.andlife.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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
        if(invitationId == null) {
            return
        }

        viewModelScope.launch {
            // TODO: 딥링크를 위한 임시 구조, 초대장 조회 후 성공 시 ID 추가 or Room DB 저장
            Log.d("DeepLink", "전달받은 ID: $invitationId")
            updateState { copy(id = invitationId) }

            _visitedInvitationIds.update { currentIds ->
                currentIds + invitationId
            }
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

    // TODO: 딥링크를 위한 임시 구조, 추후 repository 변경 필요
    companion object {
        private val _visitedInvitationIds = MutableStateFlow<Set<Long>>(emptySet())
        val visitedInvitationIds: StateFlow<Set<Long>> = _visitedInvitationIds.asStateFlow()
    }
}
