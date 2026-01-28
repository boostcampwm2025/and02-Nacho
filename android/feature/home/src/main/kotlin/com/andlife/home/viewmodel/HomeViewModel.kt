package com.andlife.home.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.andlife.domain.repository.guestbook.GuestBookRepository
import com.andlife.domain.repository.invitation.InvitationRepository
import com.andlife.home.model.HomeSideEffect
import com.andlife.home.model.HomeUiEvent
import com.andlife.home.model.HomeUiState
import com.andlife.media.audio.AudioPlaybackState
import com.andlife.media.audio.AudioPlayerManager
import com.andlife.media.video.AutoVideoPlayerPool
import com.andlife.model.guestbook.GuestBookUiModel
import com.andlife.model.guestbook.toUiModel
import com.andlife.model.invitation.UpcomingInvitationUiModel
import com.andlife.model.invitation.toUpcomingInvitationUiModel
import com.andlife.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val guestBookRepository: GuestBookRepository,
    private val invitationRepository: InvitationRepository,
    val audioPlayerManager: AudioPlayerManager,
    val videoPlayerPool: AutoVideoPlayerPool,
) : BaseViewModel<HomeUiState, HomeUiEvent, HomeSideEffect>(initialState = HomeUiState()) {
    val upcomingInvitationsPagingFlow: Flow<PagingData<UpcomingInvitationUiModel>> =
        invitationRepository.getUpcomingInvitations()
            .map { pagingData ->
                pagingData.map { it.toUpcomingInvitationUiModel() }
            }
            .cachedIn(viewModelScope)

    val guestBooksPagingFlow: Flow<PagingData<GuestBookUiModel>> =
        guestBookRepository.getAllRelatedGuestBooks()
            .map { pagingData ->
                pagingData.map { it.toUiModel() }
            }
            .cachedIn(viewModelScope)

    override val uiState: StateFlow<HomeUiState> = mutableUiState.asStateFlow()

    init {
        observeAudioPlayerState()
    }

    private fun observeAudioPlayerState() {
        audioPlayerManager.currentAudio
            .onEach { audioPlaybackState ->
                updateState {
                    copy( audioPlaybackState = audioPlaybackState ?: AudioPlaybackState())
                }
            }
            .launchIn(viewModelScope)
    }

    override fun onEvent(event: HomeUiEvent) {
        when (event) {
            is HomeUiEvent.ClickUpcomingInvitation -> navigateToDetail(event.invitationId, event.isOwner)
            is HomeUiEvent.ClickInvitationTitle -> navigateToDetail(event.invitationId, event.isOwner)
            is HomeUiEvent.ClickVisualMedia -> {}
            is HomeUiEvent.ClickAudioMedia -> clickAudioMedia(event.url)
            is HomeUiEvent.ClickVideoPlayButton -> clickVideoPlayButton(event.url, event.itemId)
            is HomeUiEvent.ClickSetting -> navigateToSetting()
            is HomeUiEvent.ClickCreate -> navigateToCreate()
            is HomeUiEvent.Refresh -> refresh()
            is HomeUiEvent.UpdateMediaPlayState -> updatePlayState(event.isPlaying)
        }
    }

    private fun clickAudioMedia(url: String) {
        val isCurrentlyPlaying = uiState.value.audioPlaybackState.isPlaying
        val currentUrl = uiState.value.audioPlaybackState.playingUrl

        if (currentUrl == url && isCurrentlyPlaying) {
            audioPlayerManager.togglePlay(url)
            videoPlayerPool.resumeLastPlayed()
        } else {
            videoPlayerPool.pauseAllPlayers()
            audioPlayerManager.togglePlay(url)
        }
    }

    private fun clickVideoPlayButton(url: String, itemId: Long) {
        val isCurrentlyPlaying = uiState.value.audioPlaybackState.isPlaying
        if (!isCurrentlyPlaying) return
        audioPlayerManager.pause()
        videoPlayerPool.playPlayer(url, itemId)
    }

    private fun navigateToDetail(invitationId: Long, isOwner: Boolean) {
        if (isOwner) {
            sendEffect(HomeSideEffect.NavigateToMyInvitationDetail(invitationId))
        } else {
            sendEffect(HomeSideEffect.NavigateToInvitationDetail(invitationId))
        }
    }

    private fun navigateToSetting() {
        sendEffect(HomeSideEffect.NavigateToSetting)
    }

    private fun navigateToCreate() {
        sendEffect(HomeSideEffect.NavigateToCreate)
    }

    private fun showMediaMessage(type: String, url: String) {
        sendEffect(HomeSideEffect.ShowMessage("$type 미디어 클릭됨: $url"))
    }

    private fun refresh() {
        updateState { copy(isRefreshing = true) }
    }

    fun onRefreshFinished(hasError: Boolean) {
        val wasUserTriggered = uiState.value.isRefreshing
        updateState { copy(isRefreshing = false) }

        if (hasError) {
            sendEffect(HomeSideEffect.RefreshFailure)
        } else {
            if (wasUserTriggered) {
                sendEffect(HomeSideEffect.ScrollToTop)
            }
        }
    }

    private fun updatePlayState(isPlaying: Boolean) {
        updateState { copy(isMediaPlaying = isPlaying) }
    }
}
