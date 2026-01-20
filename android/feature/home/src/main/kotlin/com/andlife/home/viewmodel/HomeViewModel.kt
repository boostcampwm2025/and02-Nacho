package com.andlife.home.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.andlife.domain.repository.guestbook.GuestBookRepository
import com.andlife.domain.repository.invitation.InvitationRepository
import com.andlife.domain.util.onFailure
import com.andlife.domain.util.onSuccess
import com.andlife.home.model.HomeSideEffect
import com.andlife.home.model.HomeUiEvent
import com.andlife.home.model.HomeUiState
import com.andlife.media.audio.AudioPlayerManager
import com.andlife.media.video.AutoVideoPlayerPool
import com.andlife.model.guestbook.GuestBookUiModel
import com.andlife.model.guestbook.toUiModel
import com.andlife.model.invitation.toUpcomingInvitationUiModel
import com.andlife.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val guestBookRepository: GuestBookRepository,
    private val invitationRepository: InvitationRepository,
    val audioPlayerManager: AudioPlayerManager,
    val videoPlayerPool: AutoVideoPlayerPool,
) : BaseViewModel<HomeUiState, HomeUiEvent, HomeSideEffect>(initialState = HomeUiState()) {
    override val uiState: StateFlow<HomeUiState> = mutableUiState.onStart {
        loadUpcomingInvitations()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = HomeUiState(),
    )

    val guestBooksPagingFlow: Flow<PagingData<GuestBookUiModel>> =
        guestBookRepository.getAllRelatedGuestBooks()
            .map { pagingData ->
                pagingData.map { it.toUiModel() }
            }
            .cachedIn(viewModelScope)

    init {
        observeAudioPlayerState()
    }

    private fun observeAudioPlayerState() {
        audioPlayerManager.currentAudioUrl
            .combine(audioPlayerManager.isPlaying) { url, isPlaying ->
                updateState {
                    copy(
                        playingAudioUrl = url,
                        isAudioPlaying = isPlaying,
                    )
                }
            }
            .launchIn(viewModelScope)

        uiState.map { it.isAudioPlaying }
            .distinctUntilChanged()
            .onEach { isAudioPlaying ->
                if (!isAudioPlaying) {
                    videoPlayerPool.resumeLastPlayed()
                }
            }
            .launchIn(viewModelScope)
    }

    private suspend fun loadUpcomingInvitations() {
        updateState { copy(isUpcomingLoading = true, isUpcomingError = false) }

        invitationRepository.getUpcomingInvitations()
            .onSuccess { invitations ->
                updateState {
                    copy(
                        isUpcomingLoading = false,
                        upcomingInvitations = invitations.map { it.toUpcomingInvitationUiModel() }
                    )
                }
            }.onFailure { error ->
                updateState {
                    copy(isUpcomingLoading = false, isUpcomingError = true)
                }
                Log.e("HomeViewModel", "다가오는 초대 불러오기 실패: $error")
            }
    }

    override fun onEvent(event: HomeUiEvent) {
        when (event) {
            is HomeUiEvent.ClickInvitationTitle -> navigateToDetail(event.invitationId)
            is HomeUiEvent.ClickVisualMedia -> {}
            is HomeUiEvent.ClickAudioMedia -> clickAudioMedia(event.url)
            is HomeUiEvent.ClickSetting -> navigateToSetting()
            is HomeUiEvent.ClickCreate -> navigateToCreate()
            is HomeUiEvent.RetryUpcomingLoad -> retryUpcomingLoad()
            is HomeUiEvent.RetryGuestBookLoad -> retryUpGuestBookLoad()
            is HomeUiEvent.Refresh -> refresh()
            is HomeUiEvent.ClickUpcomingInvitation -> navigateToDetail(event.invitationId)
        }
    }

    private fun clickAudioMedia(url: String) {
        val isCurrentlyPlaying = uiState.value.isAudioPlaying
        val currentUrl = uiState.value.playingAudioUrl

        if (currentUrl == url && isCurrentlyPlaying) {
            audioPlayerManager.togglePlay(url)
            videoPlayerPool.resumeLastPlayed()
        } else {
            videoPlayerPool.pauseAllPlayers()
            audioPlayerManager.togglePlay(url)
        }
    }

    private fun navigateToDetail(invitationId: Long) {
        sendEffect(HomeSideEffect.NavigateToInvitationDetail(invitationId))
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

    private fun retryUpcomingLoad() {
        viewModelScope.launch {
            loadUpcomingInvitations()
        }
    }
    private fun retryUpGuestBookLoad() {
        sendEffect(HomeSideEffect.RefreshGuestBook)
    }

    private fun refresh() {
        viewModelScope.launch {
            updateState { copy(isRefreshing = true) }

            loadUpcomingInvitations()
            sendEffect(HomeSideEffect.RefreshGuestBook)

            updateState { copy(isRefreshing = false) }
        }
    }
}
