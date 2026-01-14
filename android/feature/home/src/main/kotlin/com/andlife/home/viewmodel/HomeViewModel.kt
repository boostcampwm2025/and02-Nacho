package com.andlife.home.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.andlife.domain.repository.guestbook.GuestBookRepository
import com.andlife.domain.util.onFailure
import com.andlife.domain.util.onSuccess
import com.andlife.home.model.HomeSideEffect
import com.andlife.home.model.HomeUiEvent
import com.andlife.home.model.HomeUiState
import com.andlife.model.guestbook.toUiModel
import com.andlife.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
@Inject
constructor(
    private val guestBookRepository: GuestBookRepository,
) : BaseViewModel<HomeUiState, HomeUiEvent, HomeSideEffect>(initialState = HomeUiState()) {
    override val uiState: StateFlow<HomeUiState> =
        mutableUiState
            .onStart {
                fetchGuestBooks()
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

    private fun fetchGuestBooks() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }

            guestBookRepository
                .getGuestBooksByInvitationId(1L)
                .onSuccess { guestBooks ->
                    updateState {
                        copy(
                            isLoading = false,
                            guestBooks = guestBooks.map { it.toUiModel() }.sortedBy { it.id } // 임시로 id 기준 정렬
                        )
                    }
                }.onFailure { error ->
                    updateState { copy(isLoading = false) }
                    Log.e("HomeViewModel", "방명록 불러오기 실패: $error")
                }
        }
    }
}
