package com.andlife.home.viewmodel

import androidx.lifecycle.viewModelScope
import com.andlife.domain.repository.guestbook.GuestBookRepository
import com.andlife.home.model.HomeSideEffect
import com.andlife.home.model.HomeUiEvent
import com.andlife.home.model.HomeUiState
import com.andlife.media.video.AutoVideoPlayerPool
import com.andlife.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
@Inject
constructor(
    private val guestBookRepository: GuestBookRepository,
    val videoPlayerPool: AutoVideoPlayerPool
) : BaseViewModel<HomeUiState, HomeUiEvent, HomeSideEffect>(initialState = HomeUiState()) {
    override val uiState: StateFlow<HomeUiState> =
        mutableUiState
            .onStart {
                //fetchGuestBooks()
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = HomeUiState(),
            )

    override fun onEvent(event: HomeUiEvent) {
        when (event) {
            is HomeUiEvent.ClickInvitationTitle ->
                sendEffect(
                    HomeSideEffect.ShowMessage("초대장 제목 클릭됨: ${event.invitationId}"),
                )

            is HomeUiEvent.ClickGuestBookMenu ->
                sendEffect(
                    HomeSideEffect.ShowMessage("방명록 메뉴 클릭됨: ${event.guestBookId}"),
                )

            is HomeUiEvent.ClickVisualMedia -> sendEffect(HomeSideEffect.ShowMessage("비주얼 미디어 클릭됨: ${event.url}"))
            is HomeUiEvent.ClickAudioMedia -> sendEffect(HomeSideEffect.ShowMessage("오디오 미디어 클릭됨: ${event.url}"))
        }
    }

//    private fun fetchGuestBooks() {
//        viewModelScope.launch {
//            updateState { copy(isLoading = true) }
//
//            guestBookRepository
//                .getGuestBooksByInvitationId(1L)
//                .onSuccess { guestBooks ->
//                    updateState {
//                        copy(
//                            isLoading = false,
//                            guestBooks = guestBooks.map { it.toUiModel() },
//                        )
//                    }
//                }.onFailure { error ->
//                    updateState { copy(isLoading = false) }
//                    Log.e("HomeViewModel", "방명록 불러오기 실패: $error")
//                }
//        }
//    }
}
