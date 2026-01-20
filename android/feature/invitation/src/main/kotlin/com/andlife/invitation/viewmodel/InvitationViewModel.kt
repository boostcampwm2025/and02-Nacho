package com.andlife.invitation.viewmodel

import com.andlife.domain.repository.user.UserRepository
import com.andlife.invitation.model.InvitationSideEffect
import com.andlife.invitation.model.InvitationUiEvent
import com.andlife.invitation.model.InvitationUiState
import com.andlife.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class InvitationViewModel @Inject constructor(
    private val userRepository: UserRepository
) : BaseViewModel<InvitationUiState, InvitationUiEvent, InvitationSideEffect>(
    initialState = InvitationUiState(null)
) {
    override val uiState: StateFlow<InvitationUiState>
        get() = TODO("Not yet implemented")

    override fun onEvent(event: InvitationUiEvent) {
        TODO("Not yet implemented")
    }

}
