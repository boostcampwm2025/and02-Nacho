package com.andlife.invitation.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
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
import com.andlife.media.StoryMediaPlayerPool
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
    private val playerPool: StoryMediaPlayerPool,
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

    private var preparationStartTime = 0L
    private var currentMeasuringUrl = ""

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
        playerPool.releaseAll()
    }

    private fun loadMediaCollection() {
        viewModelScope.launch {
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

                    // 미디어 로드 후 프리캐싱 시작
                    precacheUpcomingMedia()
                }.onFailure { _, _ ->
                    updateState { copy(isLoading = false) }
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
            prepareMedia(index, selectedMedia.mediaUrl)
            precacheUpcomingMedia()
        }
    }

    private fun closeStory() {
        updateState {
            copy(
                isDetailMode = false,
                selectedIndex = -1,
            )
        }
        playerPool.pauseAll()
    }

    private fun pageChanged(index: Int) {
        updateState {
            copy(
                selectedIndex = index,
                isTextExpanded = false
            )
        }

        val selectedMedia = uiState.value.mediaItems.getOrNull(index)
        Log.d(TAG, "📄 페이지 변경: $index")
        Log.d(TAG, "선택된 미디어 타입: ${selectedMedia?.type}")

        when (selectedMedia?.type) {
            UiMediaType.VIDEO, UiMediaType.AUDIO -> {
                prepareMedia(index, selectedMedia.mediaUrl)
                precacheUpcomingMedia()
            }
            else -> {
                playerPool.pauseAll()
            }
        }
    }

    private fun prepareMedia(index: Int, url: String) {
        if (url.isEmpty()) return

        preparationStartTime = System.currentTimeMillis()
        currentMeasuringUrl = url

        // 플레이어 풀에서 플레이어 획득 및 재생
        val player = playerPool.acquirePlayer(index, url)

        // 성능 측정을 위한 리스너 추가
        player.addListener(object : androidx.media3.common.Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == androidx.media3.common.Player.STATE_READY) {
                    val duration = System.currentTimeMillis() - preparationStartTime
                    Log.d(TAG, "✅ STATE_READY | 준비 완료 시간: ${duration}ms | URL: $currentMeasuringUrl")
                }
            }

            override fun onRenderedFirstFrame() {
                val totalDuration = System.currentTimeMillis() - preparationStartTime
                Log.d(TAG, "🎬 첫 프레임 렌더링 완료 | 총 소요 시간: ${totalDuration}ms | URL: $currentMeasuringUrl")
            }
        })

        playerPool.play(index)
    }

    private fun precacheUpcomingMedia() {
        val currentIndex = uiState.value.selectedIndex
        val mediaItems = uiState.value.mediaItems

        // 현재 인덱스 기준 앞뒤 2개씩 비디오/오디오 URL 수집
        val urlsToPrecache = mutableListOf<String>()

        for (offset in -2..2) {
            val targetIndex = currentIndex + offset
            if (targetIndex in mediaItems.indices && targetIndex != currentIndex) {
                val item = mediaItems[targetIndex]
                if (item.type == UiMediaType.VIDEO || item.type == UiMediaType.AUDIO) {
                    urlsToPrecache.add(item.mediaUrl)
                }
            }
        }

        if (urlsToPrecache.isNotEmpty()) {
            playerPool.precacheVideos(urlsToPrecache)
        }
    }

    fun getPlayerForIndex(index: Int): androidx.media3.common.Player? {
        val item = uiState.value.mediaItems.getOrNull(index) ?: return null
        if (item.type != UiMediaType.VIDEO && item.type != UiMediaType.AUDIO) return null

        return playerPool.acquirePlayer(index, item.mediaUrl).getExoPlayer()
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
        private const val TAG = "Performance_After"
        private const val FILE_NAME_PREFIX = "nacho_"
    }
}
