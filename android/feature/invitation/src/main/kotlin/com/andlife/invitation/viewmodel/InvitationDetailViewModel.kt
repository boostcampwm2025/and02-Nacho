package com.andlife.invitation.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.andlife.domain.model.guestbook.GuestBookMedia
import com.andlife.domain.model.guestbook.MediaFile
import com.andlife.domain.model.guestbook.MediaType
import com.andlife.domain.repository.guestbook.GuestBookRepository
import com.andlife.domain.util.MediaFileProvider
import com.andlife.domain.util.MediaUploader
import com.andlife.domain.util.Result
import com.andlife.invitation.model.detail.InvitationDetailSideEffect
import com.andlife.invitation.model.detail.InvitationDetailUiEvent
import com.andlife.invitation.model.detail.InvitationDetailUiState
import com.andlife.ui.base.BaseViewModel
import com.andlife.ui.component.invitation.SelectedMedia
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvitationDetailViewModel
    @Inject
    constructor(
        private val mediaUploader: MediaUploader,
        private val mediaFileProvider: MediaFileProvider,
        private val guestBookRepository: GuestBookRepository,
    ) : BaseViewModel<InvitationDetailUiState, InvitationDetailUiEvent, InvitationDetailSideEffect>(
            InvitationDetailUiState(),
        ) {
        override val uiState: StateFlow<InvitationDetailUiState> =
            mutableUiState
                .onStart { loadData() }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = InvitationDetailUiState(),
                )

        private fun loadData() {
            // 초기 데이터 로드 필요한 경우 추가
        }

        override fun onEvent(event: InvitationDetailUiEvent) {
            when (event) {
                is InvitationDetailUiEvent.UpdateSelectedMedias -> updateSelectedMedias(event.medias)
                is InvitationDetailUiEvent.UpdateTextContent -> updateTextContent(event.textContent)
                is InvitationDetailUiEvent.RemoveMedia -> removeMedia(event.media)
                is InvitationDetailUiEvent.UploadMedias -> uploadMedias()
                is InvitationDetailUiEvent.ClearError -> clearError()
            }
        }

        private fun updateSelectedMedias(medias: List<SelectedMedia>) {
            updateState { copy(selectedMedias = medias) }
        }

        private fun updateTextContent(textContent: String) {
            updateState { copy(textContent = textContent) }
        }

        private fun removeMedia(media: SelectedMedia) {
            updateState {
                val currentMedias = selectedMedias.toMutableList()
                currentMedias.remove(media)
                copy(selectedMedias = currentMedias)
            }
        }

        private fun uploadMedias() {
            val medias = mutableUiState.value.selectedMedias
            if (medias.isEmpty()) return

            viewModelScope.launch {
                updateState { copy(isUploading = true) }

                try {
                    val mediaFiles =
                        mediaFileProvider.createFromUris(
                            medias.map { it.uri },
                        )

                    if (mediaFiles.isNotEmpty()) {
                        val result = mediaUploader.uploadMedias(mediaFiles)

                        when (result) {
                            is Result.Success -> {
                                val uploadedUrls = result.data.filterNotNull()

                                // 미디어 업로드 성공 후 GuestBook 생성
                                createGuestBook(uploadedUrls, mediaFiles, medias)
                            }

                            is Result.Error -> {
                                updateState { copy(isUploading = false) }
                                sendEffect(InvitationDetailSideEffect.ShowSnackbar("업로드 실패: ${result.message}"))
                            }
                        }
                    } else {
                        updateState { copy(isUploading = false) }
                        sendEffect(InvitationDetailSideEffect.ShowSnackbar("유효하지 않은 미디어 파일입니다"))
                    }
                } catch (e: Exception) {
                    updateState { copy(isUploading = false) }
                    sendEffect(InvitationDetailSideEffect.ShowSnackbar("업로드 중 오류 발생: ${e.message}"))
                }
            }
        }

        private fun clearError() {
            updateState { copy(errorMessage = null) }
        }

        private suspend fun createGuestBook(
            uploadedUrls: List<String>,
            mediaFiles: List<MediaFile>,
            selectedMedias: List<SelectedMedia>,
        ) {
            try {
                val guestBookMedias =
                    uploadedUrls.mapIndexed { index, url ->
                        val mediaFile = mediaFiles.getOrNull(index)
                        val selectedMedia = selectedMedias.getOrNull(index)
                        val thumbnailUrl =
                            if (mediaFile?.mediaType == MediaType.VIDEO) "https://thumbnailurl.com" else null // TODO: 썸네일 URL 처리
                        GuestBookMedia(
                            id = 0L,
                            type = mediaFile?.mediaType ?: MediaType.IMAGE,
                            url = url,
                            thumbnailUrl = thumbnailUrl,
                            durationSeconds = selectedMedia?.duration,
                            displayOrder = index,
                        )
                    }

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
                                selectedMedias = emptyList(),
                                textContent = "",
                                errorMessage = null,
                            )
                        }
                        sendEffect(InvitationDetailSideEffect.ShowSnackbar("방명록이 성공적으로 등록되었습니다"))
                    }

                    is Result.Error -> {
                        Log.e("InvitationDetailViewModel", "GuestBook creation failed: ${result.message}")
                        updateState { copy(isUploading = false) }
                        sendEffect(InvitationDetailSideEffect.ShowSnackbar("방명록 생성 실패: ${result.message}"))
                    }
                }
            } catch (e: Exception) {
                updateState { copy(isUploading = false) }
                sendEffect(InvitationDetailSideEffect.ShowSnackbar("방명록 생성 중 오류 발생: ${e.message}"))
            }
        }
    }
