package com.andlife.invitation.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.andlife.domain.model.guestbook.GuestBookMedia
import com.andlife.domain.model.guestbook.MediaType
import com.andlife.domain.repository.guestbook.GuestBookRepository
import com.andlife.domain.util.MediaFileProvider
import com.andlife.domain.util.MediaUploader
import com.andlife.domain.util.Result
import com.andlife.invitation.InvitationDetail
import com.andlife.invitation.model.guestbook.InvitationGuestBookSideEffect
import com.andlife.invitation.model.guestbook.InvitationGuestBookUiEvent
import com.andlife.invitation.model.guestbook.InvitationGuestBookUiState
import com.andlife.media.audio.AudioPlayerManager
import com.andlife.media.video.AutoVideoPlayerPool
import com.andlife.model.guestbook.GuestBookUiModel
import com.andlife.model.guestbook.MediaUiType
import com.andlife.model.guestbook.toUiModel
import com.andlife.ui.base.BaseViewModel
import com.andlife.ui.component.invitation.SelectedMedia
import com.andlife.model.guestbook.UiMediaType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.internal.toImmutableList
import javax.inject.Inject

@HiltViewModel
class InvitationGuestBookViewModel
@Inject
constructor(
    private val mediaUploader: MediaUploader,
    private val mediaFileProvider: MediaFileProvider,
    private val guestBookRepository: GuestBookRepository,
    val audioPlayerManager: AudioPlayerManager,
    val videoPlayerPool: AutoVideoPlayerPool,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<InvitationGuestBookUiState, InvitationGuestBookUiEvent, InvitationGuestBookSideEffect>(
    InvitationGuestBookUiState(),
) {
    private val invitationId: Long = savedStateHandle.toRoute<InvitationDetail>().id

    override val uiState: StateFlow<InvitationGuestBookUiState> = mutableUiState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val guestBooksPagingFlow: Flow<PagingData<GuestBookUiModel>> =
        guestBookRepository.getGuestBooksByInvitationId(invitationId)
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

    override fun onEvent(event: InvitationGuestBookUiEvent) {
        when (event) {
            is InvitationGuestBookUiEvent.UpdateSelectedMedias -> updateSelectedMedias(event.medias)
            is InvitationGuestBookUiEvent.UpdateTextContent -> updateTextContent(event.textContent)
            is InvitationGuestBookUiEvent.RemoveMedia -> removeMedia(event.media)
            is InvitationGuestBookUiEvent.UploadMedias -> uploadMedias()
            is InvitationGuestBookUiEvent.ClearError -> clearError()
            is InvitationGuestBookUiEvent.ClickAudioMedia -> clickAudioMedia(event.url)

            is InvitationGuestBookUiEvent.ClickGuestBookMenu -> sendEffect(
                InvitationGuestBookSideEffect.ShowSnackbar("방명록 메뉴 클릭됨: ${event.guestBookId}"),
            )

            is InvitationGuestBookUiEvent.ClickInvitationTitle -> sendEffect(
                InvitationGuestBookSideEffect.ShowSnackbar("초대장 제목 클릭됨: ${event.invitationId}"),
            )

            is InvitationGuestBookUiEvent.ClickVisualMedia -> sendEffect(
                InvitationGuestBookSideEffect.ShowSnackbar("비주얼 미디어 클릭됨: ${event.url}"),
            )

            is InvitationGuestBookUiEvent.ClickEditMenu -> startEditing(event.guestBook)
            is InvitationGuestBookUiEvent.CancelEdit -> {}
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

    private fun updateSelectedMedias(medias: List<SelectedMedia>) {
        updateState { copy(selectedMedias = medias.toPersistentList()) }
    }

    private fun updateTextContent(textContent: String) {
        updateState { copy(textContent = textContent) }
    }

    private fun removeMedia(media: SelectedMedia) {
        updateState {
            copy(selectedMedias = selectedMedias.toPersistentList().remove(media))
        }
    }

    // 등록 버튼 누를 시 호출
    private fun uploadMedias() {
        val medias = mutableUiState.value.selectedMedias
        val textContent = mutableUiState.value.textContent

        viewModelScope.launch {
            when {
                medias.isEmpty() && textContent.isBlank() -> {
                    // 아무 것도 없음
                    return@launch
                }

                medias.isEmpty() -> {
                    // 텍스트만 있는 경우
                    updateState { copy(isUploading = true) }
                    createGuestBook(emptyList(), emptyList())
                }

                else -> {
                    // 미디어가 있는 경우
                    updateState { copy(isUploading = true) }

                    try {
                        val mediaFiles =
                            mediaFileProvider.createFromUris(
                                medias.map { it.uri },
                            )

                        when {
                            mediaFiles.isEmpty() -> {
                                updateState { copy(isUploading = false) }
                                sendEffect(
                                    InvitationGuestBookSideEffect.ShowSnackbar(
                                        "유효하지 않은 미디어 파일입니다",
                                    ),
                                )
                            }

                            else -> {
                                when (val result = mediaUploader.uploadMedias(mediaFiles)) {
                                    is Result.Success -> {
                                        // 업로드된 미디어 URL 리스트: 업로드 실패한 미디어는 null 가능
                                        createGuestBook(result.data, medias)
                                    }

                                    is Result.Error -> {
                                        updateState { copy(isUploading = false) }
                                        sendEffect(
                                            InvitationGuestBookSideEffect.ShowSnackbar(
                                                "업로드 실패: ${result.message}",
                                            ),
                                        )
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        updateState { copy(isUploading = false) }
                        sendEffect(
                            InvitationGuestBookSideEffect.ShowSnackbar(
                                "업로드 중 오류 발생: ${e.message}",
                            ),
                        )
                    }
                }
            }
        }
    }


    private fun clearError() {
        updateState { copy(errorMessage = null) }
    }

    private suspend fun createGuestBook(
        uploadedUrls: List<String?>,
        selectedMedias: List<SelectedMedia>,
    ) {
        try {
            val guestBookMedias =
                uploadedUrls
                    .mapIndexed { index, url ->
                        if (url == null) return@mapIndexed null // 업로드 실패한 미디어는 건너뜀
                        val selectedMedia = selectedMedias.getOrNull(index)
                        val thumbnailUrl =
                            if (selectedMedia?.type == UiMediaType.VIDEO) "https://thumbnailurl.com" else null // TODO: 썸네일 URL 처리
                        GuestBookMedia(
                            id = 0L,
                            type =
                                when (selectedMedia?.type) {
                                    UiMediaType.IMAGE -> MediaType.IMAGE
                                    UiMediaType.VIDEO -> MediaType.VIDEO
                                    UiMediaType.AUDIO -> MediaType.AUDIO
                                    else -> MediaType.IMAGE
                                },
                            url = url,
                            thumbnailUrl = thumbnailUrl,
                            durationSeconds = selectedMedia?.duration,
                            displayOrder = index,
                        )
                    }.filterNotNull()

            val result =
                guestBookRepository.createGuestBook(
                    invitationId = 1, // 임시
                    userId = 1, // 임시
                    textContent = mutableUiState.value.textContent,
                    medias = guestBookMedias,
                )

            when (result) {
                is Result.Success -> {
                    updateState {
                        copy(
                            isUploading = false,
                            selectedMedias = persistentListOf(),
                            textContent = "",
                            errorMessage = null,
                        )
                    }
                    sendEffect(InvitationGuestBookSideEffect.CreateGuestBookSuccess)
                }

                is Result.Error -> {
                    Log.e("InvitationDetailViewModel", "GuestBook creation failed: ${result.message}")
                    updateState { copy(isUploading = false) }
                    sendEffect(InvitationGuestBookSideEffect.ShowSnackbar("방명록 생성 실패: ${result.message}"))
                }
            }
        } catch (e: Exception) {
            updateState { copy(isUploading = false) }
            sendEffect(InvitationGuestBookSideEffect.ShowSnackbar("방명록 생성 중 오류 발생: ${e.message}"))
        }
    }

    private fun startEditing(guestBook: GuestBookUiModel) {
        val existingMedias = (guestBook.visualMedias + guestBook.audioMedias)
            .sortedBy { it.displayOrder }
            .map { media ->
            SelectedMedia(
                id = media.id,
                uri = when (media.type) {
                    MediaUiType.VIDEO -> media.thumbnailUrl ?: media.url
                    MediaUiType.IMAGE, MediaUiType.AUDIO -> media.url
                },
                type = when (media.type) {
                    MediaUiType.IMAGE -> UiMediaType.IMAGE
                    MediaUiType.VIDEO -> UiMediaType.VIDEO
                    MediaUiType.AUDIO -> UiMediaType.AUDIO
                },
                duration = media.durationSeconds
            )
        }
        updateState {
            copy(
                editingGuestBookId = guestBook.id,
                textContent = guestBook.textContent,
                selectedMedias = existingMedias.toPersistentList()
            )
        }
    }
}
