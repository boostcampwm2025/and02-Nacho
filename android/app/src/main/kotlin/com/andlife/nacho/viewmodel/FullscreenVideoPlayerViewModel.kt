package com.andlife.nacho.viewmodel

import androidx.lifecycle.viewModelScope
import com.andlife.media.video.AutoVideoPlayer
import com.andlife.media.video.AutoVideoPlayerPool
import com.andlife.ui.base.BaseSideEffect
import com.andlife.ui.base.BaseUiEvent
import com.andlife.ui.base.BaseUiState
import com.andlife.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FullscreenVideoPlayerUiState(
    val isMuted: Boolean = false
) : BaseUiState

sealed interface FullscreenVideoPlayerEvent : BaseUiEvent {
    data class Initialize(val videoUrl: String) : FullscreenVideoPlayerEvent

    data class Foreground(val videoUrl: String) : FullscreenVideoPlayerEvent

    data class Background(val videoUrl: String) : FullscreenVideoPlayerEvent

    data object ToggleMute : FullscreenVideoPlayerEvent
}

sealed interface FullscreenVideoPlayerSideEffect : BaseSideEffect {

}

@HiltViewModel
class FullscreenVideoPlayerViewModel @Inject constructor(
    val videoPlayerPool: AutoVideoPlayerPool,
) : BaseViewModel<FullscreenVideoPlayerUiState, FullscreenVideoPlayerEvent, FullscreenVideoPlayerSideEffect>(initialState = FullscreenVideoPlayerUiState()) {

    override val uiState: StateFlow<FullscreenVideoPlayerUiState> = mutableUiState.asStateFlow()

    init {
        observeMuteState()
    }

    fun getPlayer(videoUrl: String): AutoVideoPlayer = videoPlayerPool.getPlayer(videoUrl)

    override fun onEvent(event: FullscreenVideoPlayerEvent) {
        when (event) {
            is FullscreenVideoPlayerEvent.Initialize -> videoPlayerPool.getPlayer(event.videoUrl)

            is FullscreenVideoPlayerEvent.Foreground -> videoPlayerPool.getPlayer(event.videoUrl).play()

            is FullscreenVideoPlayerEvent.Background -> videoPlayerPool.getPlayer(event.videoUrl).pause()

            FullscreenVideoPlayerEvent.ToggleMute -> videoPlayerPool.toggleMute()
        }
    }

    private fun observeMuteState() {
        viewModelScope.launch {
            videoPlayerPool.isMuted.collect { isMuted ->
                updateState { copy(isMuted = isMuted) }
            }
        }
    }
}
