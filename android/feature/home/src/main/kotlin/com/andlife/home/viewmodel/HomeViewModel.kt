package com.andlife.home.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.andlife.domain.model.guestbook.Author
import com.andlife.domain.model.guestbook.GuestBook
import com.andlife.domain.model.guestbook.GuestBookInvitation
import com.andlife.domain.model.guestbook.GuestBookMedia
import com.andlife.domain.repository.guestbook.GuestBookRepository
import com.andlife.domain.util.onFailure
import com.andlife.domain.util.onSuccess
import com.andlife.ui.base.BaseSideEffect
import com.andlife.ui.base.BaseUiEvent
import com.andlife.ui.base.BaseUiState
import com.andlife.ui.base.BaseViewModel
import com.andlife.ui.model.GuestBookEntryMediaUiModel
import com.andlife.ui.model.UiMediaType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import javax.inject.Inject

fun GuestBook.toUiModel(): GuestBookUiModel = GuestBookUiModel(
    id = id,
    invitation = invitation,
    author = author.toUiModel(),
    textContent = textContent,
    visualMedias = visualMedias.map { it.toUiModel() }.toImmutableList(),
    audioMedias = audioMedias.map { it.toUiModel() }.toImmutableList(),
    totalVisualCount = totalVisualCount,
    isOwner = isOwner,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun GuestBookMedia.toUiModel(): GuestBookEntryMediaUiModel = GuestBookEntryMediaUiModel(
    id = id,
    type = UiMediaType.safeValueOf(type.name),
    url = url,
    thumbnailUrl = thumbnailUrl,
    durationSeconds = durationSeconds,
    displayOrder = displayOrder,
)

fun Author.toUiModel(): GuestBookAuthorUiModel = GuestBookAuthorUiModel(
    id = id,
    name = name,
    profileImageUrl = profileImageUrl,
)

fun GuestBookInvitation.toUiModel(): GuestBookInvitationUiModel = GuestBookInvitationUiModel(
    id = id,
    title = title,
)

data class GuestBookUiModel(
    val id: Long,
    val invitation: GuestBookInvitation,
    val author: GuestBookAuthorUiModel,
    val textContent: String,
    val visualMedias: ImmutableList<GuestBookEntryMediaUiModel>,
    val audioMedias: ImmutableList<GuestBookEntryMediaUiModel>,
    val totalVisualCount: Int,
    val isOwner: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

data class GuestBookAuthorUiModel(
    val id: Long,
    val name: String,
    val profileImageUrl: String? = null,
)

data class GuestBookInvitationUiModel(
    val id: Long,
    val title: String,
)


data class HomeUiState(
    val isLoading: Boolean = false,
    val guestBooks: List<GuestBookUiModel> = emptyList()
) : BaseUiState

sealed interface HomeUiEvent : BaseUiEvent {
    // UI 이벤트 정의
}

sealed interface HomeSideEffect : BaseSideEffect {
    // 사이드 이펙트 정의
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val guestBookRepository: GuestBookRepository
) : BaseViewModel<HomeUiState, HomeUiEvent, HomeSideEffect>(initialState = HomeUiState()) {

    override val uiState: StateFlow<HomeUiState> = mutableUiState
        .onStart {
            fetchGuestBooks()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = HomeUiState()
        )

    override fun onEvent(event: HomeUiEvent) {

    }

    private fun fetchGuestBooks() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }

            guestBookRepository.getGuestBooksByInvitationId(1L)
                .onSuccess { guestBooks ->
                    updateState {
                        copy(
                            isLoading = false,
                            guestBooks = guestBooks.map { it.toUiModel() }
                        )
                    }
                }
                .onFailure { error ->
                    updateState { copy(isLoading = false) }
                    Log.e("HomeViewModel", "방명록 불러오기 실패: $error")
                }
        }
    }
}
