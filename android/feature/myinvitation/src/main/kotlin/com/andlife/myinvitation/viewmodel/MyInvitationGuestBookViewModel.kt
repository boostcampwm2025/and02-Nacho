package com.andlife.myinvitation.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.andlife.domain.error.DataError
import com.andlife.domain.model.auth.AuthState
import com.andlife.domain.model.guestbook.GuestBook
import com.andlife.domain.model.guestbook.GuestBookMedia
import com.andlife.domain.model.guestbook.MediaType
import com.andlife.domain.repository.auth.AuthStateManager
import com.andlife.domain.repository.guestbook.GuestBookRepository
import com.andlife.domain.repository.report.ReportRepository
import com.andlife.domain.util.AnalyticsEvent
import com.andlife.domain.util.AnalyticsLogger
import com.andlife.domain.util.Button
import com.andlife.domain.util.CrashlyticsLogger
import com.andlife.domain.util.EventType
import com.andlife.domain.util.MediaFileProvider
import com.andlife.domain.util.MediaUploader
import com.andlife.domain.util.RefreshEventHub
import com.andlife.domain.util.RefreshEventHub.RefreshTarget
import com.andlife.domain.util.Result
import com.andlife.domain.util.Screen
import com.andlife.domain.util.ThumbnailGenerator
import com.andlife.domain.util.onFailure
import com.andlife.domain.util.onSuccess
import com.andlife.media.audio.AudioPlaybackState
import com.andlife.media.audio.AudioPlayerManager
import com.andlife.media.video.AutoVideoPlayerPool
import com.andlife.model.common.ReportReason
import com.andlife.model.common.ReportTargetType
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
import com.andlife.ui.util.media.validateSelectedMediasByRule
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
class MyInvitationGuestBookViewModel
@Inject
constructor(
    private val mediaUploader: MediaUploader,
    private val mediaFileProvider: MediaFileProvider,
    private val thumbnailGenerator: ThumbnailGenerator,
    private val guestBookRepository: GuestBookRepository,
    private val reportRepository: ReportRepository,
    private val authStateManager: AuthStateManager,
    val audioPlayerManager: AudioPlayerManager,
    val videoPlayerPool: AutoVideoPlayerPool,
    private val analyticsLogger: AnalyticsLogger,
    private val crashlyticsLogger: CrashlyticsLogger,
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
        observeAuthState()
        observeAudioPlayerState()
    }

    private fun observeAuthState() {
        authStateManager.authState
            .onEach { authState ->
                val isStateChanged = uiState.value.isAuthStateChanged(authState)
                updateState { copy( authState = authState ) }
                if (isStateChanged) {
                    sendEffect(MyInvitationGuestBookSideEffect.AuthStateChanged(authState) )
                }
            }
            .launchIn(viewModelScope)
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

    override fun onEvent(event: MyInvitationGuestBookUiEvent) {
        when (event) {
            is MyInvitationGuestBookUiEvent.UpdateSelectedMedias -> updateSelectedMedias(
                event.medias,
            )

            is MyInvitationGuestBookUiEvent.UpdateTextContent -> updateTextContent(event.textContent)
            is MyInvitationGuestBookUiEvent.RemoveMedia -> removeMedia(event.media)
            is MyInvitationGuestBookUiEvent.UploadMedias -> {
                analyticsLogger.logEvent(AnalyticsEvent.ButtonClick(Screen.MY_INVITATION_DETAIL_GUEST_BOOK, Button.UPLOAD_GUEST_BOOK))
                handleUploadMedias()
            }
            is MyInvitationGuestBookUiEvent.ClickCamera -> handleCameraClick()
            is MyInvitationGuestBookUiEvent.ClickMicrophone -> handleMicrophoneClick()
            is MyInvitationGuestBookUiEvent.ClearError -> clearError()
            is MyInvitationGuestBookUiEvent.ClickAudioMedia -> clickAudioMedia(event.url)
            is MyInvitationGuestBookUiEvent.ClickVideoPlayButton -> clickVideoPlayButton(event.url, event.itemId)
            is MyInvitationGuestBookUiEvent.ClickVisualMedia -> {}
            is MyInvitationGuestBookUiEvent.ClickEditMenu -> {
                analyticsLogger.logEvent(AnalyticsEvent.ButtonClick(Screen.MY_INVITATION_DETAIL_GUEST_BOOK, Button.UPDATE_GUEST_BOOK))
                startEditing(event.guestBook)
            }
            is MyInvitationGuestBookUiEvent.CancelEdit -> cancelEdit()
            is MyInvitationGuestBookUiEvent.ClickDeleteMenu -> {
                analyticsLogger.logEvent(AnalyticsEvent.ButtonClick(Screen.MY_INVITATION_DETAIL_GUEST_BOOK, Button.DELETE_GUEST_BOOK))
                deleteGuestBook(event.guestBookId)
            }
            is MyInvitationGuestBookUiEvent.UpdateMediaPlayState -> updatePlayState(event.isPlaying)
            MyInvitationGuestBookUiEvent.Refresh -> refresh()
            MyInvitationGuestBookUiEvent.CheckLogin -> checkLogin()
            MyInvitationGuestBookUiEvent.DismissLoginDialog -> dismissLoginDialog()
            is MyInvitationGuestBookUiEvent.ShowReport -> updateReportTargetId(event.guestBookId)
            MyInvitationGuestBookUiEvent.DismissReport -> updateReportTargetId(null)
            is MyInvitationGuestBookUiEvent.SubmitReport -> {
                analyticsLogger.logEvent(AnalyticsEvent.ButtonClick(Screen.MY_INVITATION_DETAIL_GUEST_BOOK, Button.INVITATION_REPORT))
                submitReport(event.reason, event.description)
            }
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

    private fun updateSelectedMedias(
        medias: List<SelectedMedia>,
    ) {
        // 용량/개수 검증
        val (validatedMedias, exceededAvailableBytes, exceededAvailableSlots) = validateSelectedMediasByRule(
            selectedMedias = medias,
            availableSlotCnt = 20 - uiState.value.selectedMedias.size,
            currentMediaSizeBytes = uiState.value.currentMediaSizeBytes
        )

        updateState {
            copy(
                selectedMedias = validatedMedias.toPersistentList(),
                currentMediaSizeBytes = calculateTotalMediaSize(medias)
            )
        }

        // 용량 초과로 거부된 파일이 있으면 스낵바로 알림
        if (exceededAvailableBytes) {
            analyticsLogger.logEvent(AnalyticsEvent.Event(EventType.GUEST_BOOK_MAX_SIZE.value))
            sendEffect(
                MyInvitationGuestBookSideEffect.ShowSnackbar(
                    "파일이 500MB를 초과하여 제외되었습니다."
                )
            )
        }

        // 제외된 파일이 있으면 스낵바로 알림 (후순위)
        else if (exceededAvailableSlots) {
            analyticsLogger.logEvent(AnalyticsEvent.Event(EventType.GUEST_BOOK_MAX_MEDIA.value))
            sendEffect(
                MyInvitationGuestBookSideEffect.ShowSnackbar(
                    "파일은 20개까지만 추가 가능합니다."
                )
            )
        }
    }

    private fun calculateTotalMediaSize(medias: List<SelectedMedia>): Long {
        var totalMediaSize = 0L
        medias.map { media ->
            val fileSize = media.sizeBytes
            if (fileSize != null) {
                totalMediaSize += fileSize
            } else {
                Log.d("GuestBookViewModel", "Media size: size unknown")
            }
        }
        return totalMediaSize
    }

    private fun updateTextContent(textContent: String) {
        updateState { copy(textContent = textContent) }
    }

    private fun removeMedia(media: SelectedMedia) {
        val state = uiState.value
        val updatedMedias = state.selectedMedias.toPersistentList().remove(media)
        updateState {
            copy(
                selectedMedias = updatedMedias,
                currentMediaSizeBytes = calculateTotalMediaSize(updatedMedias)
            )
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
                crashlyticsLogger.log(e.message ?: "upload Error")
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
        if (authStateManager.authState.value !is AuthState.Authenticated) {
            updateState { copy(isUploading = false) }
            sendEffect(MyInvitationGuestBookSideEffect.ShowSnackbar("로그인이 필요합니다."))
            return
        }

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
        if (authStateManager.authState.value !is AuthState.Authenticated) {
            updateState { copy(isUploading = false) }
            sendEffect(MyInvitationGuestBookSideEffect.ShowSnackbar("로그인이 필요합니다."))
            return
        }

        val existingImageIds =
            allSelectedMedias.filter { it.id != null && it.type == UiMediaType.IMAGE }.mapNotNull { it.id }
        val existingAudioIds =
            allSelectedMedias.filter { it.id != null && it.type == UiMediaType.AUDIO }.mapNotNull { it.id }
        val existingVideoIds =
            allSelectedMedias.filter { it.id != null && it.type == UiMediaType.VIDEO }.mapNotNull { it.id }

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
                RefreshEventHub.emit(RefreshTarget.HOME)
            }

            is Result.Error -> sendEffect(MyInvitationGuestBookSideEffect.ShowSnackbar("실패: ${result.message}"))
        }
    }

    private fun deleteGuestBook(guestBookId: Long) {
        if (authStateManager.authState.value !is AuthState.Authenticated) {
            updateState { copy(isUploading = false) }
            sendEffect(MyInvitationGuestBookSideEffect.ShowSnackbar("로그인이 필요합니다."))
            return
        }

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
                .onFailure { error, msg ->
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
                currentMediaSizeBytes = 0L,
            )
        }
    }

    private fun handleCameraClick() {
        val state = uiState.value
        if (state.selectedMedias.size >= 5) {
            sendEffect(MyInvitationGuestBookSideEffect.ShowSnackbar("최대 5개까지 미디어를 추가할 수 있습니다."))
            return
        }
        sendEffect(MyInvitationGuestBookSideEffect.LaunchCamera)
    }

    private fun handleMicrophoneClick() {
        val state = uiState.value
        if (state.selectedMedias.size >= 5) {
            sendEffect(MyInvitationGuestBookSideEffect.ShowSnackbar("최대 5개까지 미디어를 추가할 수 있습니다."))
            return
        }
        updateState { copy(isMediaPlaying = false, audioRecordingDuration = 0) }
        audioPlayerManager.pause()
        sendEffect(MyInvitationGuestBookSideEffect.ShowAudioRecordingBottomSheet)
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
            sendEffect(MyInvitationGuestBookSideEffect.RefreshFailure)
        } else {
            if (wasUserTriggered) {
                sendEffect(MyInvitationGuestBookSideEffect.ScrollToTop)
            }
        }
    }

    private fun checkLogin() {
        val isAuthenticated = authStateManager.authState.value is AuthState.Authenticated
        if (!isAuthenticated) {
            updateState { copy(showLoginDialog = true) }
        }
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
                analyticsLogger.logEvent(AnalyticsEvent.Event(EventType.SUCCESS_REPORT_GUESTBOOK.value))
                updateState { copy(reportTargetId = null) }
                sendEffect(MyInvitationGuestBookSideEffect.ReportSuccess)
            }.onFailure { error, message ->
                analyticsLogger.logEvent(
                    AnalyticsEvent.Event(
                        EventType.FAIL_REPORT_GUESTBOOK.value, mapOf(
                            "id" to targetId.toString(),
                            "error" to "$error: $message"
                        )
                    )
                )
                crashlyticsLogger.recordException("$targetId: $error - $message")
                val messageToShow = if (error == DataError.Network.CONFLICT) {
                    message
                } else {
                    null
                }
                sendEffect(MyInvitationGuestBookSideEffect.ReportFailure(messageToShow))
            }
        }
    }
}
