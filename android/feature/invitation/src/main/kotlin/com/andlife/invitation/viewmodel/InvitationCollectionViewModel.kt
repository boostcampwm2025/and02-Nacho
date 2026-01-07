package com.andlife.invitation.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.andlife.domain.repository.guestbook.GuestBookRepository
import com.andlife.domain.util.onFailure
import com.andlife.domain.util.onSuccess
import com.andlife.invitation.model.guestbook.collection.InvitationCollectionSideEffect
import com.andlife.invitation.model.guestbook.collection.InvitationCollectionUiEvent
import com.andlife.invitation.model.guestbook.collection.InvitationCollectionUiState
import com.andlife.invitation.model.guestbook.collection.toUiModel
import com.andlife.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class InvitationCollectionViewModel @Inject constructor(
    private val guestBookRepository: GuestBookRepository,
) : BaseViewModel<InvitationCollectionUiState, InvitationCollectionUiEvent, InvitationCollectionSideEffect>(
        initialState = InvitationCollectionUiState(),
    ) {
    override val uiState: StateFlow<InvitationCollectionUiState> =
        mutableUiState
            .onStart {
                loadMediaCollection(1L)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = InvitationCollectionUiState(),
            )

    override fun onEvent(event: InvitationCollectionUiEvent) {
        when (event) {
            is InvitationCollectionUiEvent.OpenStory -> openStory(event.index)
            is InvitationCollectionUiEvent.CloseStory -> closeStory()
            is InvitationCollectionUiEvent.PageChanged -> pageChanged(event.index)
        }
    }

    private suspend fun loadMediaCollection(invitationId: Long) {
        updateState { copy(isLoading = true) }

        guestBookRepository
            .getMediaCollection(invitationId)
            .onSuccess { mediaList ->
                updateState {
                    copy(
                        isLoading = false,
                        mediaItems = mediaList.map { it.toUiModel() }.toImmutableList(),
                    )
                }
                Log.d("ViewModel", "미디어 리스트: $mediaList")
            }.onFailure {
                updateState { copy(isLoading = false) }
                Log.e("ViewModel", "에러 발생: $it")
            }
    }

    private fun openStory(index: Int) {
        updateState {
            copy(
                isDetailMode = true,
                selectedIndex = index,
            )
        }
        Log.d("ViewModel", "선택된 인덱스: $index")
    }

    private fun closeStory() {
        updateState {
            copy(
                isDetailMode = false,
                selectedIndex = -1,
            )
        }
    }

    private fun pageChanged(index: Int) {
        updateState {
            copy(
                selectedIndex = index,
            )
        }
        Log.d("ViewModel", "바뀐 인덱스: $index")
    }
}
