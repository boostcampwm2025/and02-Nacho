package com.andlife.invitation.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.andlife.domain.model.guestbook.GuestBookMedia
import com.andlife.domain.model.guestbook.MediaType
import com.andlife.domain.repository.guestbook.GuestBookRepository
import com.andlife.domain.util.MediaFileProvider
import com.andlife.domain.util.MediaUploader
import com.andlife.domain.util.Result
import com.andlife.invitation.model.guestbook.InvitationGuestBookSideEffect
import com.andlife.invitation.model.guestbook.InvitationGuestBookUiEvent
import com.andlife.invitation.model.guestbook.InvitationGuestBookUiState
import com.andlife.ui.base.BaseViewModel
import com.andlife.ui.component.invitation.SelectedMedia
import com.andlife.model.guestbook.UiMediaType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvitationGuestBookViewModel
@Inject
constructor(
    private val mediaUploader: MediaUploader,
    private val mediaFileProvider: MediaFileProvider,
    private val guestBookRepository: GuestBookRepository,
) : BaseViewModel<InvitationGuestBookUiState, InvitationGuestBookUiEvent, InvitationGuestBookSideEffect>(
    InvitationGuestBookUiState(),
) {
    override val uiState: StateFlow<InvitationGuestBookUiState> =
        mutableUiState
            .onStart { loadData() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = InvitationGuestBookUiState(),
            )

    private fun loadData() {
        // 초기 데이터 로드 필요한 경우 추가
    }

    override fun onEvent(event: InvitationGuestBookUiEvent) {
        when (event) {
            is InvitationGuestBookUiEvent.UpdateSelectedMedias -> updateSelectedMedias(event.medias)
            is InvitationGuestBookUiEvent.UpdateTextContent -> updateTextContent(event.textContent)
            is InvitationGuestBookUiEvent.RemoveMedia -> removeMedia(event.media)
            is InvitationGuestBookUiEvent.UploadMedias -> uploadMedias()
            is InvitationGuestBookUiEvent.ClearError -> clearError()
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
                    sendEffect(InvitationGuestBookSideEffect.ShowSnackbar("방명록이 성공적으로 등록되었습니다"))
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
}
