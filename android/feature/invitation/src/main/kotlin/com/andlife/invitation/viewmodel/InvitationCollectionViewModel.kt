package com.andlife.invitation.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.andlife.domain.repository.guestbook.GuestBookRepository
import com.andlife.domain.util.onFailure
import com.andlife.domain.util.onSuccess
import com.andlife.invitation.model.guestbook.collection.InvitationCollectionSideEffect
import com.andlife.invitation.model.guestbook.collection.InvitationCollectionUiEvent
import com.andlife.invitation.model.guestbook.collection.InvitationCollectionUiState
import com.andlife.invitation.model.guestbook.collection.toUiModel
import com.andlife.ui.base.BaseViewModel
import com.andlife.ui.model.UiMediaType
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
class InvitationCollectionViewModel
    @Inject
    constructor(
        private val guestBookRepository: GuestBookRepository,
        @param:ApplicationContext private val context: Context,
    ) : BaseViewModel<InvitationCollectionUiState, InvitationCollectionUiEvent, InvitationCollectionSideEffect>(
            initialState = InvitationCollectionUiState(),
        ) {
        override val uiState: StateFlow<InvitationCollectionUiState> = mutableUiState

        private var invitationId: Long by Delegates.notNull<Long>()

        val exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ONE
            playWhenReady = true
        }

        override fun onEvent(event: InvitationCollectionUiEvent) {
            when (event) {
                is InvitationCollectionUiEvent.OpenStory -> openStory(event.index)
                is InvitationCollectionUiEvent.CloseStory -> closeStory()
                is InvitationCollectionUiEvent.PageChanged -> pageChanged(event.index)
                is InvitationCollectionUiEvent.ToggleExpand -> toggleExpand()
            }
        }

        override fun onCleared() {
            super.onCleared()
            exoPlayer.release()
        }

        fun initInvitationId(id: Long) {
            runCatching { invitationId }.onSuccess { if (it == id) return }

            invitationId = id
            loadMediaCollection()
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
                    }.onFailure {
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
            updateState { copy(selectedIndex = index) }

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
