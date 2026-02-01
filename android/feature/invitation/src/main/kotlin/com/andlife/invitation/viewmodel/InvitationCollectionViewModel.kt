package com.andlife.invitation.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.toRoute
import com.andlife.domain.model.guestbook.DownloadState
import com.andlife.domain.model.guestbook.MediaType
import com.andlife.domain.repository.guestbook.GuestBookRepository
import com.andlife.domain.repository.user.UserRepository
import com.andlife.domain.util.MediaDownloader
import com.andlife.domain.util.onFailure
import com.andlife.domain.util.onSuccess
import com.andlife.invitation.InvitationDetail
import com.andlife.invitation.model.collection.InvitationCollectionSideEffect
import com.andlife.invitation.model.collection.InvitationCollectionUiEvent
import com.andlife.invitation.model.collection.InvitationCollectionUiState
import com.andlife.model.collection.toUiModel
import com.andlife.model.guestbook.UiMediaType
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
class InvitationCollectionViewModel @Inject constructor(
    private val guestBookRepository: GuestBookRepository,
    private val mediaDownloader: MediaDownloader,
    private val userRepository: UserRepository,
    @param:ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<InvitationCollectionUiState, InvitationCollectionUiEvent, InvitationCollectionSideEffect>(
    initialState = InvitationCollectionUiState(),
) {

    private val invitationId: Long = savedStateHandle.toRoute<InvitationDetail>().id

    override val uiState: StateFlow<InvitationCollectionUiState> =
        mutableUiState
            .onStart {
                loadMediaCollection()

                val dismissed = userRepository.isWifiDialogDismissed()
                updateState { copy(networkDialogDismissed = dismissed) }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = InvitationCollectionUiState(isLoading = true),
            )

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
            is InvitationCollectionUiEvent.DownloadMedia -> downloadCurrentMedia()
            is InvitationCollectionUiEvent.DisableNetworkDialogPermanently -> disableNetworkDialogPermanently()
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
                }.onFailure { it, _ ->
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

    private fun downloadCurrentMedia() {
        val item = uiState.value.mediaItems.getOrNull(uiState.value.selectedIndex) ?: return

        viewModelScope.launch {
            if (!userRepository.isFirstDownloadDone()) {
                userRepository.setFirstDownloadDone()
                sendEffect(InvitationCollectionSideEffect.ShowDownloadGuide)
            }
        }

        downloadMedia(item.mediaUrl, item.type)
    }

    private fun downloadMedia(url: String, mediaType: UiMediaType) {
        if (uiState.value.downloadingUrls.contains(url)) return

        val domainType = when (mediaType) {
            UiMediaType.IMAGE -> MediaType.IMAGE
            UiMediaType.VIDEO -> MediaType.VIDEO
            UiMediaType.AUDIO -> MediaType.AUDIO
        }
        val fileName = "$FILE_NAME_PREFIX${System.currentTimeMillis()}${domainType.getExtension()}"

        updateState { copy(downloadingUrls = downloadingUrls + url) }

        val workId = mediaDownloader.enqueueDownload(url, fileName, domainType)

        viewModelScope.launch {
            mediaDownloader.getDownloadStatus(workId).collect { state ->
                updateState { copy(downloadState = state) }
                when (state) {
                    is DownloadState.Success -> {
                        updateState { copy(downloadingUrls = downloadingUrls - url) }
                        updateState { copy(downloadState = DownloadState.Idle) }
                    }

                    is DownloadState.Error -> {
                        updateState { copy(downloadingUrls = downloadingUrls - url) }
                        sendEffect(InvitationCollectionSideEffect.DownloadFailed)
                        updateState { copy(downloadState = DownloadState.Idle) }
                    }

                    else -> {}
                }
            }
        }
    }

    private fun disableNetworkDialogPermanently() {
        updateState { copy(networkDialogDismissed = true) }

        viewModelScope.launch {
            userRepository.setWifiDialogDismissed()
        }
    }

    companion object {
        private const val FILE_NAME_PREFIX = "nacho_"
    }
}
