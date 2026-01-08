package com.andlife.myinvitation.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.andlife.domain.repository.guestbook.GuestBookRepository
import com.andlife.domain.util.onFailure
import com.andlife.domain.util.onSuccess
import com.andlife.myinvitation.model.MyInvitationCollectionSideEffect
import com.andlife.myinvitation.model.MyInvitationCollectionUiEvent
import com.andlife.myinvitation.model.MyInvitationCollectionUiState
import com.andlife.myinvitation.model.toUiModel
import com.andlife.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MyInvitationCollectionViewModel @Inject constructor(
    private val guestBookRepository: GuestBookRepository,
) : BaseViewModel<MyInvitationCollectionUiState, MyInvitationCollectionUiEvent, MyInvitationCollectionSideEffect>(
        initialState = MyInvitationCollectionUiState(),
    ) {
    override val uiState: StateFlow<MyInvitationCollectionUiState> =
        mutableUiState
            .onStart {
                loadMediaCollection(1L)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = MyInvitationCollectionUiState(),
            )

    override fun onEvent(event: MyInvitationCollectionUiEvent) {
        when (event) {
            is MyInvitationCollectionUiEvent.OpenStory -> openStory(event.index)
            is MyInvitationCollectionUiEvent.CloseStory -> closeStory()
            is MyInvitationCollectionUiEvent.PageChanged -> pageChanged(event.index)
            is MyInvitationCollectionUiEvent.ToggleExpand -> toggleExpand()
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

    private fun toggleExpand() {
        updateState {
            copy(
                isTextExpanded = !isTextExpanded,
            )
        }
    }
}
