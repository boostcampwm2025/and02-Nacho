package com.andlife.invitation.viewmodel

import com.andlife.invitation.model.detail.InvitationDetailSideEffect
import com.andlife.invitation.model.detail.InvitationDetailUiEvent
import com.andlife.invitation.model.detail.InvitationDetailUiState
import com.andlife.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class InvitationDetailViewModel @Inject constructor(

) : BaseViewModel<InvitationDetailUiState, InvitationDetailUiEvent, InvitationDetailSideEffect>(InvitationDetailUiState()){
    override val uiState: StateFlow<InvitationDetailUiState>
        get() = TODO("Not yet implemented")

    override fun onEvent(event: InvitationDetailUiEvent) {
        TODO("Not yet implemented")
    }

}
