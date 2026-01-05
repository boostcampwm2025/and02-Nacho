package com.andlife.invitation.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.andlife.domain.repository.guestbook.GuestBookRepository
import com.andlife.domain.util.onFailure
import com.andlife.domain.util.onSuccess
import com.andlife.invitation.model.InvitationSideEffect
import com.andlife.invitation.model.InvitationUiEvent
import com.andlife.invitation.model.InvitationUiState
import com.andlife.invitation.model.guestbook.toUiModel
import com.andlife.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvitationViewModel @Inject constructor(
    private val guestBookRepository: GuestBookRepository
) : BaseViewModel<InvitationUiState, InvitationUiEvent, InvitationSideEffect>(
    initialState = InvitationUiState()
) {

    override val uiState: StateFlow<InvitationUiState> = mutableUiState
        .onStart {
            loadMediaCollection(1L)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = InvitationUiState()
        )

    override fun onEvent(event: InvitationUiEvent) {
        TODO("Not yet implemented")
    }

    private fun loadMediaCollection(invitationId: Long) {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }

            guestBookRepository.getMediaCollection(invitationId)
                .onSuccess { mediaList ->
                    updateState {
                        copy(
                            isLoading = false,
                            mediaItems = mediaList.map { it.toUiModel() }
                        )
                    }
                    Log.d("ViewModel", "미디어 리스트: $mediaList")
                }
                .onFailure {
                    updateState { copy(isLoading = false) }
                    Log.e("ViewModel", "에러 발생: $it")
                }
        }
    }
}
