package com.andlife.myinvitation.viewmodel

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import androidx.navigation.toRoute
import com.andlife.domain.model.guestbook.DownloadState
import com.andlife.domain.model.guestbook.MediaType
import com.andlife.domain.repository.guestbook.GuestBookRepository
import com.andlife.domain.repository.user.UserRepository
import com.andlife.domain.util.AnalyticsEvent
import com.andlife.domain.util.AnalyticsLogger
import com.andlife.domain.util.Button
import com.andlife.domain.util.CrashlyticsLogger
import com.andlife.domain.util.MediaDownloader
import com.andlife.domain.util.Screen
import com.andlife.domain.util.onFailure
import com.andlife.domain.util.onSuccess
import com.andlife.model.collection.toUiModel
import com.andlife.model.guestbook.UiMediaType
import com.andlife.myinvitation.MyInvitationDetail
import com.andlife.myinvitation.model.collection.MyInvitationCollectionSideEffect
import com.andlife.myinvitation.model.collection.MyInvitationCollectionUiEvent
import com.andlife.myinvitation.model.collection.MyInvitationCollectionUiState
import com.andlife.media.StoryMediaPlayerPool
import com.andlife.ui.base.BaseViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel(assistedFactory = MyInvitationCollectionViewModel.Factory::class)
class MyInvitationCollectionViewModel @AssistedInject constructor(
    private val guestBookRepository: GuestBookRepository,
    private val mediaDownloader: MediaDownloader,
    private val userRepository: UserRepository,
    val playerPool: StoryMediaPlayerPool,
    private val analyticsLogger: AnalyticsLogger,
    private val crashlyticsLogger: CrashlyticsLogger,
    @param:ApplicationContext private val context: Context,
    @Assisted private val invitationId: Long,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<MyInvitationCollectionUiState, MyInvitationCollectionUiEvent, MyInvitationCollectionSideEffect>(
    initialState = MyInvitationCollectionUiState(),
) {
    override val uiState: StateFlow<MyInvitationCollectionUiState> =
        mutableUiState
            .onStart {
                loadMediaCollection()
                val dismissed = userRepository.isWifiDialogDismissed()
                updateState { copy(networkDialogDismissed = dismissed) }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = MyInvitationCollectionUiState(isLoading = true),
            )

    override fun onEvent(event: MyInvitationCollectionUiEvent) {
        when (event) {
            is MyInvitationCollectionUiEvent.OpenStory -> openStory(event.index)
            is MyInvitationCollectionUiEvent.CloseStory -> closeStory()
            is MyInvitationCollectionUiEvent.PageChanged -> pageChanged(event.index)
            is MyInvitationCollectionUiEvent.ToggleExpand -> toggleExpand()
            is MyInvitationCollectionUiEvent.DownloadMedia -> {
                analyticsLogger.logEvent(AnalyticsEvent.ButtonClick(Screen.MY_INVITATION_DETAIL_COLLECTION, Button.MEDIA_DOWNLOAD))
                downloadCurrentMedia()
            }
            is MyInvitationCollectionUiEvent.DisableNetworkDialogPermanently -> disableNetworkDialogPermanently()
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
                    precacheUpcomingMedia()
                }.onFailure { _, _ ->
                    updateState { copy(isLoading = false) }
                }
        }
    }

    private fun openStory(index: Int) {
        updateState { copy(isDetailMode = true, selectedIndex = index) }
        val selectedMedia = uiState.value.mediaItems.getOrNull(index)
        if (selectedMedia?.type == UiMediaType.VIDEO || selectedMedia?.type == UiMediaType.AUDIO) {
            playMediaWithStrategy(index, selectedMedia.mediaUrl)
            precacheUpcomingMedia()
        }
    }

    private fun closeStory() {
        updateState { copy(isDetailMode = false, selectedIndex = -1) }
        playerPool.pauseAll()
    }

    private fun pageChanged(index: Int) {
        updateState { copy(selectedIndex = index, isTextExpanded = false) }
        val selectedMedia = uiState.value.mediaItems.getOrNull(index)

        when (selectedMedia?.type) {
            UiMediaType.VIDEO, UiMediaType.AUDIO -> {
                playMediaWithStrategy(index, selectedMedia.mediaUrl)
                precacheUpcomingMedia()
            }
            else -> playerPool.pauseAll()
        }
    }

    private fun playMediaWithStrategy(index: Int, url: String) {
        if (url.isEmpty()) return
        playerPool.acquirePlayer(index, url)
        playerPool.play(index)
    }

    private fun precacheUpcomingMedia() {
        val currentIndex = uiState.value.selectedIndex
        if (currentIndex == -1) return

        val mediaItems = uiState.value.mediaItems
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

    fun getPlayerForIndex(index: Int): Player? {
        val item = uiState.value.mediaItems.getOrNull(index) ?: return null
        if (item.type != UiMediaType.VIDEO && item.type != UiMediaType.AUDIO) return null

        return try {
            playerPool.acquirePlayer(index, item.mediaUrl).getExoPlayer()
        } catch (e: Exception) {
            null
        }
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
                sendEffect(MyInvitationCollectionSideEffect.ShowDownloadGuide)
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
                        crashlyticsLogger.log(state.message)
                        updateState { copy(downloadingUrls = downloadingUrls - url) }
                        sendEffect(MyInvitationCollectionSideEffect.DownloadFailed)
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


    @AssistedFactory
    interface Factory {
        fun create(
            myInvitationId: Long,
        ): MyInvitationCollectionViewModel
    }
}
