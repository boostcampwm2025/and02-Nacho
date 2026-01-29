package com.andlife.myinvitation.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.toRoute
import com.andlife.domain.repository.guestbook.GuestBookRepository
import com.andlife.domain.util.onFailure
import com.andlife.domain.util.onSuccess
import com.andlife.model.guestbook.UiMediaType
import com.andlife.model.collection.toUiModel
import com.andlife.myinvitation.MyInvitationDetail
import com.andlife.myinvitation.model.collection.MyInvitationCollectionSideEffect
import com.andlife.myinvitation.model.collection.MyInvitationCollectionUiEvent
import com.andlife.myinvitation.model.collection.MyInvitationCollectionUiState
import com.andlife.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyInvitationCollectionViewModel
    @Inject
    constructor(
        private val guestBookRepository: GuestBookRepository,
        @param:ApplicationContext private val context: Context,
        savedStateHandle: SavedStateHandle
    ) : BaseViewModel<MyInvitationCollectionUiState, MyInvitationCollectionUiEvent, MyInvitationCollectionSideEffect>(
            initialState = MyInvitationCollectionUiState(),
        ) {
        private val invitationId: Long = savedStateHandle.toRoute<MyInvitationDetail>().id

    override val uiState: StateFlow<MyInvitationCollectionUiState> =
        mutableUiState
            .onStart {
                loadMediaCollection()
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = MyInvitationCollectionUiState(isLoading = true),
            )

    val exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build().apply {
        repeatMode = Player.REPEAT_MODE_ONE
        playWhenReady = true
    }

    override fun onEvent(event: MyInvitationCollectionUiEvent) {
        when (event) {
            is MyInvitationCollectionUiEvent.OpenStory -> openStory(event.index)
            is MyInvitationCollectionUiEvent.CloseStory -> closeStory()
            is MyInvitationCollectionUiEvent.PageChanged -> pageChanged(event.index)
            is MyInvitationCollectionUiEvent.ToggleExpand -> toggleExpand()
        }
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer.release()
    }

    private fun loadMediaCollection() {
        viewModelScope.launch {
            Log.d("ViewModel", "id:$invitationId")
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
                }.onFailure { it, msg ->
                    updateState { copy(isLoading = false) }
                    Log.e("ViewModel", "에러 발생: $it")
                }
        }
    }

    private fun openStory(index: Int) {
        updateState {
            copy(
                isDetailMode = true,
                selectedIndex = index,
            )
        }
        val selectedMedia = uiState.value.mediaItems.getOrNull(index)

        if (selectedMedia?.type == UiMediaType.VIDEO || selectedMedia?.type == UiMediaType.AUDIO) {
            prepareMedia(selectedMedia.mediaUrl)
        }
    }

    private fun closeStory() {
        updateState {
            copy(
                isDetailMode = false,
                selectedIndex = -1,
            )
        }
        exoPlayer.pause()
    }

    private fun pageChanged(index: Int) {
        updateState {
            copy(
                selectedIndex = index,
                isTextExpanded = false
            )
        }

        val selectedMedia = uiState.value.mediaItems.getOrNull(index)

        when (selectedMedia?.type) {
            UiMediaType.VIDEO, UiMediaType.AUDIO -> {
                prepareMedia(selectedMedia.mediaUrl)
            }
            else -> {
                exoPlayer.pause()
            }
        }
    }

    private fun prepareMedia(url: String) {
        if (url.isEmpty()) return

        val currentUri = exoPlayer.currentMediaItem?.localConfiguration?.uri?.toString()

        if (currentUri == url) {
            exoPlayer.seekTo(0)
            exoPlayer.play()
            return
        }

        exoPlayer.stop()
        exoPlayer.clearMediaItems()

        val mediaItem = MediaItem.fromUri(url)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    private fun toggleExpand() {
        updateState {
            copy(
                isTextExpanded = !isTextExpanded,
            )
        }
    }
}
