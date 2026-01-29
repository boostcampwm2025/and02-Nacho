package com.andlife.invitation.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.andlife.domain.error.DataError
import com.andlife.domain.model.guestbook.GuestBook
import com.andlife.domain.model.guestbook.GuestBookMedia
import com.andlife.domain.model.guestbook.MediaType
import com.andlife.domain.repository.guestbook.GuestBookRepository
import com.andlife.domain.util.MediaFileProvider
import com.andlife.domain.util.MediaUploader
import com.andlife.domain.util.Result
import com.andlife.domain.util.ThumbnailGenerator
import com.andlife.domain.util.onFailure
import com.andlife.domain.util.onSuccess
import com.andlife.invitation.InvitationDetail
import com.andlife.invitation.model.guestbook.InvitationGuestBookSideEffect
import com.andlife.invitation.model.guestbook.InvitationGuestBookUiEvent
import com.andlife.invitation.model.guestbook.InvitationGuestBookUiState
import com.andlife.media.audio.AudioPlaybackState
import com.andlife.media.audio.AudioPlayerManager
import com.andlife.media.video.AutoVideoPlayerPool
import com.andlife.model.guestbook.GuestBookUiModel
import com.andlife.model.guestbook.MediaUiType
import com.andlife.model.guestbook.UiMediaType
import com.andlife.model.guestbook.toUiModel
import com.andlife.ui.base.BaseViewModel
import com.andlife.ui.component.invitation.SelectedMedia
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvitationGuestBookViewModel
@Inject
constructor(
    private val mediaUploader: MediaUploader,
    private val mediaFileProvider: MediaFileProvider,
    private val thumbnailGenerator: ThumbnailGenerator,
    private val guestBookRepository: GuestBookRepository,
    val audioPlayerManager: AudioPlayerManager,
    val videoPlayerPool: AutoVideoPlayerPool,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<InvitationGuestBookUiState, InvitationGuestBookUiEvent, InvitationGuestBookSideEffect>(
    InvitationGuestBookUiState(),
) {
    private val invitationId: Long = savedStateHandle.toRoute<InvitationDetail>().id

    override val uiState: StateFlow<InvitationGuestBookUiState> = mutableUiState.asStateFlow()

    private val refreshFlow = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val guestBooksPagingFlow: Flow<PagingData<GuestBookUiModel>> =
        refreshFlow.flatMapLatest {
            guestBookRepository.getGuestBooksByInvitationId(invitationId)
                .map { pagingData ->
                    pagingData.map { it.toUiModel() }
                }
        }.cachedIn(viewModelScope)

    init {
        observeAudioPlayerState()
    }

    private fun observeAudioPlayerState() {
        audioPlayerManager.currentAudio
            .onEach { audioPlaybackState ->
                updateState {
                    copy( audioPlaybackState = audioPlaybackState ?: AudioPlaybackState() )
                }
            }
            .launchIn(viewModelScope)
    }


    override fun onEvent(event: InvitationGuestBookUiEvent) {
        when (event) {
            is InvitationGuestBookUiEvent.UpdateSelectedMedias -> updateSelectedMedias(event.medias)
            is InvitationGuestBookUiEvent.UpdateTextContent -> updateTextContent(event.textContent)
            is InvitationGuestBookUiEvent.RemoveMedia -> removeMedia(event.media)
            is InvitationGuestBookUiEvent.UploadMedias -> handleUploadMedias()
            is InvitationGuestBookUiEvent.ClickCamera -> handleCameraClick()
            is InvitationGuestBookUiEvent.ClickMicrophone -> handleMicrophoneClick()
            is InvitationGuestBookUiEvent.ClearError -> clearError()
            is InvitationGuestBookUiEvent.ClickAudioMedia -> clickAudioMedia(event.url)
            is InvitationGuestBookUiEvent.ClickVideoPlayButton -> clickVideoPlayButton(event.url, event.itemId)

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
            is InvitationGuestBookUiEvent.CancelEdit -> cancelEdit()
            is InvitationGuestBookUiEvent.ClickDeleteMenu -> deleteGuestBook(event.guestBookId)
            is InvitationGuestBookUiEvent.UpdateMediaPlayState -> updatePlayState(event.isPlaying)
            InvitationGuestBookUiEvent.Refresh -> refresh()
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

    private fun clearError() {
        updateState { copy(errorMessage = null) }
    }

    private fun handleCameraClick() {
        val state = uiState.value
        if (state.selectedMedias.size >= 5) {
            sendEffect(InvitationGuestBookSideEffect.ShowSnackbar("최대 5개까지 미디어를 추가할 수 있습니다."))
            return
        }
        sendEffect(InvitationGuestBookSideEffect.LaunchCamera)
    }

    private fun startEditing(guestBook: GuestBookUiModel) {
        val existingMedias = (guestBook.visualMedias + guestBook.audioMedias)
            .sortedBy { it.displayOrder }
            .map { media ->
                SelectedMedia(
                    id = media.id,
                    uri = media.url,
                    type = when (media.type) {
                        MediaUiType.IMAGE -> UiMediaType.IMAGE
                        MediaUiType.VIDEO -> UiMediaType.VIDEO
                        MediaUiType.AUDIO -> UiMediaType.AUDIO
                    },
                    duration = media.durationSeconds,
                    thumbnailUrl = media.thumbnailUrl
                )
            }
        updateState {
            copy(
                editingGuestBookId = guestBook.id,
                textContent = guestBook.textContent,
                selectedMedias = existingMedias.toPersistentList(),
                originalTextContent = guestBook.textContent,
                originalMediaIds = existingMedias.mapNotNull { it.id }.toSet(),
            )
        }
    }

    private fun handleUploadMedias() {
        val state = uiState.value
        if (!state.isSubmittable) return

        viewModelScope.launch {
            updateState { copy(isUploading = true) }

            val newMedias = state.selectedMedias.filter { it.id == null }

            try {
                val (uploadedUrls, thumbnailUrls) = if (newMedias.isNotEmpty()) {
                    val mediaFiles = mediaFileProvider.createFromUris(newMedias.map { it.uri })
                    when (val uploadResult = mediaUploader.uploadMedias(mediaFiles)) {
                        is Result.Success -> {
                            val thumbnails = generateAndUploadThumbnails(newMedias)
                            uploadResult.data to thumbnails
                        }
                        is Result.Error -> {
                            updateState { copy(isUploading = false) }
                            sendEffect(InvitationGuestBookSideEffect.ShowSnackbar("업로드 실패: ${uploadResult.message}"))
                            return@launch
                        }
                    }
                } else {
                    emptyList<String?>() to emptyList<String?>()
                }

                if (state.editingGuestBookId == null) {
                    Log.d("qqqqq", "방명록 생성")
                    createGuestBook(uploadedUrls, thumbnailUrls, newMedias)
                } else {
                    Log.d("qqqqq", "방명록 수정: ${state.editingGuestBookId}")
                    updateGuestBook(state.editingGuestBookId, uploadedUrls, thumbnailUrls, state.selectedMedias)
                }
            } catch (e: Exception) {
                updateState { copy(isUploading = false) }
                sendEffect(InvitationGuestBookSideEffect.ShowSnackbar("업로드 중 오류 발생: ${e.message}"))
                return@launch
            }
        }
    }

    private suspend fun generateAndUploadThumbnails(
        medias: List<SelectedMedia>
    ): List<String?> {
        return medias.map { media ->
            if (media.type != UiMediaType.VIDEO) return@map null

            try {
                val thumbnailFile = thumbnailGenerator.generateVideoThumbnail(media.uri)

                if (thumbnailFile == null) {
                    Log.e("ThumbnailProcess", "썸네일 파일 생성 실패")
                    return@map null
                }

                Log.d("ThumbnailProcess", "썸네일 파일 생성: ${thumbnailFile.absolutePath}")

                val thumbnailMediaFile = mediaFileProvider.createFromFile(thumbnailFile)

                Log.d("ThumbnailProcess", "썸네일 MediaFile 생성: $thumbnailMediaFile")
                when (val result = mediaUploader.uploadMedias(listOf(thumbnailMediaFile))) {
                    is Result.Success -> result.data.firstOrNull()
                    is Result.Error -> {
                        Log.e("ThumbnailUpload", "썸네일 업로드 실패: ${result.message}")
                        null
                    }
                }
            } catch (e: Exception) {
                Log.e("ThumbnailProcess", "썸네일 처리 실패", e)
                null
            }
        }
    }

    private suspend fun createGuestBook(
        uploadedUrls: List<String?>,
        thumbnailUrls: List<String?>,
        newSelectedMedias: List<SelectedMedia>
    ) {
        val guestBookMedias = uploadedUrls
            .mapIndexedNotNull { index, url ->
                val urlValue = url ?: return@mapIndexedNotNull null
                val selectedMedia = newSelectedMedias.getOrNull(index) ?: return@mapIndexedNotNull null
                val thumbnailUrl = thumbnailUrls.getOrNull(index)

                GuestBookMedia(
                    id = 0L,
                    type = when (selectedMedia.type) {
                        UiMediaType.IMAGE -> MediaType.IMAGE
                        UiMediaType.VIDEO -> MediaType.VIDEO
                        UiMediaType.AUDIO -> MediaType.AUDIO
                    },
                    url = urlValue,
                    thumbnailUrl = thumbnailUrl,
                    durationSeconds = selectedMedia.duration,
                    displayOrder = index,
                )
            }

        val result = guestBookRepository.createGuestBook(
            invitationId = invitationId,
            userId = 1,
            textContent = uiState.value.textContent,
            medias = guestBookMedias,
        )
        handleResult(result)
    }

    private suspend fun updateGuestBook(
        guestBookId: Long,
        uploadedUrls: List<String?>,
        thumbnailUrls: List<String?>,
        allSelectedMedias: List<SelectedMedia>
    ) {
        val existingImageIds = allSelectedMedias.filter { it.id != null && it.type == UiMediaType.IMAGE }.mapNotNull { it.id }
        val existingAudioIds = allSelectedMedias.filter { it.id != null && it.type == UiMediaType.AUDIO }.mapNotNull { it.id }
        val existingVideoIds = allSelectedMedias.filter { it.id != null && it.type == UiMediaType.VIDEO }.mapNotNull { it.id }

        val onlyNewMedias = allSelectedMedias.filter { it.id == null }

        val newMedias = uploadedUrls
            .mapIndexedNotNull { index, url ->
                val urlValue = url ?: return@mapIndexedNotNull null
                val selectedMedia = onlyNewMedias.getOrNull(index) ?: return@mapIndexedNotNull null
                val thumbnailUrl = thumbnailUrls.getOrNull(index)

                GuestBookMedia(
                    id = 0L,
                    type = when (selectedMedia.type) {
                        UiMediaType.IMAGE -> MediaType.IMAGE
                        UiMediaType.VIDEO -> MediaType.VIDEO
                        UiMediaType.AUDIO -> MediaType.AUDIO
                    },
                    url = urlValue,
                    thumbnailUrl = thumbnailUrl,
                    durationSeconds = selectedMedia.duration,
                    displayOrder = allSelectedMedias.indexOf(selectedMedia)
                )
            }

        val result = guestBookRepository.updateGuestBook(
            guestBookId = guestBookId,
            textContent = uiState.value.textContent,
            existingImageIds = existingImageIds,
            existingVideoIds = existingVideoIds,
            existingAudioIds = existingAudioIds,
            newMedias = newMedias
        )
        handleResult(result, true)
    }

    private fun handleResult(result: Result<GuestBook, DataError>, isUpdate: Boolean = false) = viewModelScope.launch {
        updateState { copy(isUploading = false) }
        when (result) {
            is Result.Success -> {
                clearFormInput()
                if (isUpdate) {
                    //videoPlayerPool.clearCacheById(result.data.id)
                    sendEffect(InvitationGuestBookSideEffect.UpdateGuestBookSuccess)
                } else {
                    sendEffect(InvitationGuestBookSideEffect.CreateGuestBookSuccess)
                }
            }

            is Result.Error -> sendEffect(InvitationGuestBookSideEffect.ShowSnackbar("실패: ${result.message}"))
        }
    }

    private fun deleteGuestBook(guestBookId: Long) {
        viewModelScope.launch {
            guestBookRepository.deleteGuestBook(guestBookId)
                .onSuccess { deletedId ->
                    updateState {
                        copy(
                            editingGuestBookId = if (editingGuestBookId == deletedId) null else editingGuestBookId,
                            selectedMedias = if (editingGuestBookId == deletedId) persistentListOf() else selectedMedias,
                            textContent = if (editingGuestBookId == deletedId) "" else textContent,
                            originalTextContent = if (editingGuestBookId == deletedId) "" else originalTextContent,
                            originalMediaIds = if (editingGuestBookId == deletedId) setOf() else originalMediaIds,
                        )
                    }
                    sendEffect(InvitationGuestBookSideEffect.DeleteGuestBookSuccess)
                }
                .onFailure { error, msg ->
                    sendEffect(InvitationGuestBookSideEffect.ShowSnackbar("방명록 삭제를 실패하였습니다."))
                }
        }
    }

    fun invalidateGuestBooks() {
        Log.d("qqqqq", "방명록 목록 무효화")
        refreshFlow.value += 1
    }

    private fun cancelEdit() = clearFormInput()

    private fun clearFormInput() {
        updateState {
            copy(
                textContent = "",
                selectedMedias = persistentListOf(),
                editingGuestBookId = null,
                originalTextContent = "",
                originalMediaIds = emptySet(),
            )
        }
    }

    private fun handleMicrophoneClick() {
        val state = uiState.value
        if (state.isAudioRecording) return
        if (state.selectedMedias.size >= 5) {
            sendEffect(InvitationGuestBookSideEffect.ShowSnackbar("최대 5개까지 미디어를 추가할 수 있습니다."))
            return
        }
        updateState { copy(isAudioRecording = true, audioRecordingDuration = 0) }
        sendEffect(InvitationGuestBookSideEffect.StartAudioRecording)
    }

    private fun updatePlayState(isPlaying: Boolean) {
        updateState { copy(isMediaPlaying = isPlaying) }
    }

    private fun refresh() {
        invalidateGuestBooks()
        updateState { copy(isRefreshing = true) }
    }

    fun onRefreshFinished(hasError: Boolean) {
        val wasUserTriggered = uiState.value.isRefreshing
        updateState { copy(isRefreshing = false) }

        if (hasError) {
            sendEffect(InvitationGuestBookSideEffect.RefreshFailure)
        } else {
            if (wasUserTriggered) {
                sendEffect(InvitationGuestBookSideEffect.ScrollToTop)
            }
        }
    }
}
