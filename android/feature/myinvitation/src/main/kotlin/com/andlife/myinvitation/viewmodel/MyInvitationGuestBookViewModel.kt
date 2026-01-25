package com.andlife.myinvitation.viewmodel

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
import com.andlife.media.audio.AudioPlayerManager
import com.andlife.media.video.AutoVideoPlayerPool
import com.andlife.model.guestbook.GuestBookUiModel
import com.andlife.model.guestbook.MediaUiType
import com.andlife.model.guestbook.UiMediaType
import com.andlife.model.guestbook.toUiModel
import com.andlife.myinvitation.MyInvitationDetail
import com.andlife.myinvitation.model.guestbook.MyInvitationGuestBookSideEffect
import com.andlife.myinvitation.model.guestbook.MyInvitationGuestBookUiEvent
import com.andlife.myinvitation.model.guestbook.MyInvitationGuestBookUiState
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyInvitationGuestBookViewModel
@Inject
constructor(
    private val mediaUploader: MediaUploader,
    private val mediaFileProvider: MediaFileProvider,
    private val thumbnailGenerator: ThumbnailGenerator,
    private val guestBookRepository: GuestBookRepository,
    val audioPlayerManager: AudioPlayerManager,
    val videoPlayerPool: AutoVideoPlayerPool,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<MyInvitationGuestBookUiState, MyInvitationGuestBookUiEvent, MyInvitationGuestBookSideEffect>(
    MyInvitationGuestBookUiState(),
) {
    private val invitationId: Long = savedStateHandle.toRoute<MyInvitationDetail>().id

    override val uiState: StateFlow<MyInvitationGuestBookUiState> = mutableUiState.asStateFlow()

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

    override fun onEvent(event: MyInvitationGuestBookUiEvent) {
        when (event) {
            is MyInvitationGuestBookUiEvent.UpdateSelectedMedias -> updateSelectedMedias(event.medias)
            is MyInvitationGuestBookUiEvent.UpdateTextContent -> updateTextContent(event.textContent)
            is MyInvitationGuestBookUiEvent.RemoveMedia -> removeMedia(event.media)
            is MyInvitationGuestBookUiEvent.UploadMedias -> handleUploadMedias()
            is MyInvitationGuestBookUiEvent.ClearError -> clearError()
            is MyInvitationGuestBookUiEvent.ClickAudioMedia -> clickAudioMedia(event.url)

            is MyInvitationGuestBookUiEvent.ClickGuestBookMenu -> sendEffect(
                MyInvitationGuestBookSideEffect.ShowSnackbar("방명록 메뉴 클릭됨: ${event.guestBookId}"),
            )

            is MyInvitationGuestBookUiEvent.ClickInvitationTitle -> sendEffect(
                MyInvitationGuestBookSideEffect.ShowSnackbar("초대장 제목 클릭됨: ${event.invitationId}"),
            )

            is MyInvitationGuestBookUiEvent.ClickVisualMedia -> sendEffect(
                MyInvitationGuestBookSideEffect.ShowSnackbar("비주얼 미디어 클릭됨: ${event.url}"),
            )

            is MyInvitationGuestBookUiEvent.ClickEditMenu -> startEditing(event.guestBook)
            is MyInvitationGuestBookUiEvent.CancelEdit -> cancelEdit()
            is MyInvitationGuestBookUiEvent.ClickDeleteMenu -> deleteGuestBook(event.guestBookId)
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

    private fun clearError() {
        updateState { copy(errorMessage = null) }
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
                            sendEffect(MyInvitationGuestBookSideEffect.ShowSnackbar("업로드 실패: ${uploadResult.message}"))
                            return@launch
                        }
                    }
                } else {
                    emptyList<String?>() to emptyList()
                }

                if (state.editingGuestBookId == null) {
                    createGuestBook(uploadedUrls, thumbnailUrls, newMedias)
                } else {
                    updateGuestBook(state.editingGuestBookId, uploadedUrls, thumbnailUrls, state.selectedMedias)
                }
            } catch (e: Exception) {
                updateState { copy(isUploading = false) }
                sendEffect(MyInvitationGuestBookSideEffect.ShowSnackbar("업로드 중 오류 발생: ${e.message}"))
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
                val thumbnailFile = thumbnailGenerator.generateVideoThumbnail(media.uri) ?: return@map null

                val thumbnailMediaFile = mediaFileProvider.createFromFile(thumbnailFile)

                when (val result = mediaUploader.uploadMedias(listOf(thumbnailMediaFile))) {
                    is Result.Success -> result.data.firstOrNull()
                    is Result.Error -> {
                        null
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
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
                    sendEffect(MyInvitationGuestBookSideEffect.UpdateGuestBookSuccess)
                } else {
                    sendEffect(MyInvitationGuestBookSideEffect.CreateGuestBookSuccess)
                }
            }

            is Result.Error -> sendEffect(MyInvitationGuestBookSideEffect.ShowSnackbar("실패: ${result.message}"))
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
                    sendEffect(MyInvitationGuestBookSideEffect.DeleteGuestBookSuccess)
                }
                .onFailure {
                    sendEffect(MyInvitationGuestBookSideEffect.ShowSnackbar("방명록 삭제를 실패하였습니다."))
                }
        }
    }

    fun invalidateGuestBooks() {
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
}
