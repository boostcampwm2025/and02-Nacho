package com.andlife.home.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.andlife.domain.error.DataError
import com.andlife.domain.model.auth.AuthState
import com.andlife.domain.repository.auth.AuthStateManager
import com.andlife.domain.repository.guestbook.GuestBookRepository
import com.andlife.domain.repository.invitation.InvitationRepository
import com.andlife.domain.repository.report.ReportRepository
import com.andlife.domain.util.onFailure
import com.andlife.domain.util.onSuccess
import com.andlife.home.model.HomeSideEffect
import com.andlife.home.model.HomeUiEvent
import com.andlife.home.model.HomeUiState
import com.andlife.media.audio.AudioPlaybackState
import com.andlife.media.audio.AudioPlayerManager
import com.andlife.media.video.AutoVideoPlayerPool
import com.andlife.model.common.ReportReason
import com.andlife.model.common.ReportTargetType
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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val guestBookRepository: GuestBookRepository,
    private val invitationRepository: InvitationRepository,
    private val reportRepository: ReportRepository,
    private val authStateManager: AuthStateManager,
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
                    copy(audioPlaybackState = audioPlaybackState ?: AudioPlaybackState())
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
            is HomeUiEvent.UpdateMediaPlayState -> updatePlayState(event.isPlaying)
            HomeUiEvent.DismissLoginDialog -> dismissLoginDialog()
            is HomeUiEvent.ShowReport -> updateReportTargetId(event.targetId)
            HomeUiEvent.DismissReport -> updateReportTargetId(null)
            is HomeUiEvent.SubmitReport -> submitReport(event.reason, event.description)
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
        val isAuthenticated = authStateManager.authState.value is AuthState.Authenticated
        if (!isAuthenticated) {
            updateState { copy(showLoginDialog = true) }
        } else {
            sendEffect(HomeSideEffect.NavigateToCreate)
        }
    }

    private fun showMediaMessage(type: String, url: String) {
        sendEffect(HomeSideEffect.ShowMessage("$type 미디어 클릭됨: $url"))
    }

    private fun updatePlayState(isPlaying: Boolean) {
        updateState { copy(isMediaPlaying = isPlaying) }
    }

    fun handleRefresh() {
        sendEffect(HomeSideEffect.NeedRefresh)
    }

    private fun dismissLoginDialog() {
        updateState { copy(showLoginDialog = false) }
    }

    private fun updateReportTargetId(targetId: Long?) {
        val authState = authStateManager.authState.value
        if (authState !is AuthState.Authenticated) {
            updateState { copy(showLoginDialog = true) }
            return
        }
        updateState { copy(reportTargetId = targetId) }
    }

    private fun submitReport(reason: ReportReason, description: String?) {
        val targetId = uiState.value.reportTargetId ?: return
        viewModelScope.launch {
            reportRepository.sendReport(
                targetType = ReportTargetType.GUESTBOOK.name,
                targetId = targetId,
                reason = reason.name,
                description = description
            ).onSuccess {
                updateState { copy(reportTargetId = null) }
                sendEffect(HomeSideEffect.ReportSuccess)
            }.onFailure { error, message ->
                val messageToShow = if (error == DataError.Network.CONFLICT) {
                    message
                } else {
                    null
                }
                sendEffect(HomeSideEffect.ReportFailure(messageToShow))
            }
        }
    }
}
