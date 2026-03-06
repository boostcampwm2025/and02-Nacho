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
import com.andlife.domain.model.guestbook.UploadGuestBookState
import com.andlife.domain.repository.auth.AuthStateManager
import com.andlife.domain.repository.guestbook.GuestBookRepository
import com.andlife.domain.repository.report.ReportRepository
import com.andlife.domain.util.AnalyticsEvent
import com.andlife.domain.util.AnalyticsLogger
import com.andlife.domain.util.BackgroundMediaUploader
import com.andlife.domain.util.Button
import com.andlife.domain.util.CrashlyticsLogger
import com.andlife.domain.util.EventType
import com.andlife.domain.util.MediaFileCopyManager
import com.andlife.domain.util.RefreshEventHub
import com.andlife.domain.util.RefreshEventHub.RefreshTarget
import com.andlife.domain.util.Screen
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
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class MyInvitationGuestBookViewModel
@Inject
constructor(
    private val backgroundMediaUploader: BackgroundMediaUploader,
    private val guestBookRepository: GuestBookRepository,
    private val reportRepository: ReportRepository,
    private val authStateManager: AuthStateManager,
    private val mediaFileCopyManager: MediaFileCopyManager,
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
                updateState { copy(authState = authState) }
                if (isStateChanged) {
                    sendEffect(MyInvitationGuestBookSideEffect.AuthStateChanged(authState))
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
                handleUploadAndSubmit()
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
        )

        Log.d("MyInvitationGuestBookVM", "업데이트된 미디어의 uri: ${validatedMedias.map { it.uri }}")
        // 새로 추가된 미디어 중 content uri인 파일들을 내부 저장소로 복사
        val newContentUriMedias = validatedMedias.filter { media ->
            media.id == null && media.uri.startsWith("content://")
        }
        if (newContentUriMedias.isNotEmpty()) {
            updateState { copy(isProcessingMedia = true) }
            viewModelScope.launch {
                try {
                    val copiedUris = mediaFileCopyManager.copyFilesToInternal(
                        newContentUriMedias.map { it.uri }
                    )
                    // 복사된 URI로 업데이트된 미디어 리스트 생성
                    val updatedMedias = validatedMedias.map { media ->
                        val newContentUriIndex = newContentUriMedias.indexOfFirst { it.uri == media.uri }
                        if (newContentUriIndex >= 0) {
                            // content uri 파일인 경우 복사된 경로로 교체
                            val copiedUri = copiedUris[newContentUriIndex]
                            media.copy(uri = copiedUri ?: media.uri)  // TODO: 복사에 실패한 경우 기존 URI 반환하고있음
                        } else {
                            media
                        }
                    }

                    updateState {
                        copy(
                            selectedMedias = updatedMedias.toPersistentList(),
                            currentMediaSizeBytes = calculateTotalMediaSize(updatedMedias),
                            isProcessingMedia = false
                        )
                    }
                    Log.d("MyInvitationGuestBookVM", "파일 복사 완료, 업데이트된 미디어: ${updatedMedias.map { it.uri }}")

                } catch (e: Exception) {
                    Log.e("MyInvitationGuestBookVM", "파일 복사 중 오류", e)
                    updateState { copy(isProcessingMedia = false) }
                    sendEffect(MyInvitationGuestBookSideEffect.ShowSnackbar("파일 처리 중 오류가 발생했습니다"))
                }
            }
        } else {
            // content uri인 파일이 없는 경우 바로 업데이트
            updateState {
                copy(
                    selectedMedias = validatedMedias.toPersistentList(),
                    currentMediaSizeBytes = calculateTotalMediaSize(validatedMedias)
                )
            }
        }

        // 용량 초과로 거부된 파일이 있으면 스낵바로 알림
        if (exceededAvailableBytes) {
            analyticsLogger.logEvent(AnalyticsEvent.Event(EventType.GUEST_BOOK_MAX_SIZE.value))
            sendEffect(
                MyInvitationGuestBookSideEffect.ShowSnackbar(
                    "최대 파일 용량을 초과하여 제외되었습니다."
                )
            )
        }

        // 제외된 파일이 있으면 스낵바로 알림 (후순위)
        else if (exceededAvailableSlots) {
            analyticsLogger.logEvent(AnalyticsEvent.Event(EventType.GUEST_BOOK_MAX_MEDIA.value))
            sendEffect(
                MyInvitationGuestBookSideEffect.ShowSnackbar(
                    "최대 파일 개수를 초과하여 제외되었습니다."
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
                    uri = media.url,  // 기존 미디어의 URL이 들어감
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

    private fun handleUploadAndSubmit() {
        val state = uiState.value
        if (!state.isSubmittable) return

        updateState { copy(isUploading = true) }

        // 각 필드를 json으로 직렬화 -> worker에서 역직렬화해서 GuestBookMedia 리스트로 만들어 사용
        val selectedMediasId = state.selectedMedias.map { it.id } // 기존 미디어는 ID, 새 미디어는 null이므로 구분 가능
        val selectedMediasUri = state.selectedMedias.map { it.uri } // 기존 미디어는 URI 대신 URL을 사용
        val selectedMediasType = state.selectedMedias.map { it.type.name }
        val selectedMediasDuration = state.selectedMedias.map { it.duration }
        val selectedMediasThumbnailUrl = state.selectedMedias.map { it.thumbnailUrl }

        // 백그라운드 업로드 시작
        val pairOfWorkIds = backgroundMediaUploader.uploadMediasInBackground(
            invitationId = invitationId,
            guestBookText = state.textContent,
            editingGuestBookId = state.editingGuestBookId ?: -1L, // 새 방명록인 경우 -1로 전달
            selectedMediasId = Json.encodeToString(selectedMediasId),
            selectedMediasUri = Json.encodeToString(selectedMediasUri),
            selectedMediasType = Json.encodeToString(selectedMediasType),
            selectedMediasDuration = Json.encodeToString(selectedMediasDuration),
            selectedMediasThumbnailUrl = Json.encodeToString(selectedMediasThumbnailUrl),
        )

        val uploadWorkId = pairOfWorkIds.first
        val guestBookWorkId = pairOfWorkIds.second

        Log.d("BackgroundUpload", "WorkID: $uploadWorkId, $guestBookWorkId")

        // 업로드 진행상황 관찰
        viewModelScope.launch {
            backgroundMediaUploader.observeUploadProgress(uploadWorkId to guestBookWorkId)
                .collect { uploadGuestBookState ->
                    handleUploadGuestBookStateChange(uploadGuestBookState, state)
                }
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
                isProcessingMedia = false,
            )
        }
    }

    private fun handleCameraClick() {
        val state = uiState.value
        if (state.selectedMedias.size >= 20) {
            sendEffect(MyInvitationGuestBookSideEffect.ShowSnackbar("최대 20개까지 미디어를 추가할 수 있습니다."))
            return
        }
        sendEffect(MyInvitationGuestBookSideEffect.LaunchCamera)
    }

    private fun handleMicrophoneClick() {
        val state = uiState.value
        if (state.selectedMedias.size >= 20) {
            sendEffect(MyInvitationGuestBookSideEffect.ShowSnackbar("최대 20개까지 미디어를 추가할 수 있습니다."))
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


    private fun handleUploadGuestBookStateChange(
        uploadGuestBookState: UploadGuestBookState,
        originalState: MyInvitationGuestBookUiState
    ) {
        Log.d("BackgroundUpload", "업로드 상태 변화: $uploadGuestBookState")

        when (uploadGuestBookState) {
            is UploadGuestBookState.Enqueued -> {
                Log.d("BackgroundUpload", "업로드 대기 중")
            }

            is UploadGuestBookState.Progress -> {
                Log.d(
                    "BackgroundUpload",
                    "업로드 진행: ${uploadGuestBookState.percent}% (${uploadGuestBookState.currentOrder}/${uploadGuestBookState.totalCount})"
                )
            }

            is UploadGuestBookState.Success -> {
                Log.d("BackgroundUpload", "업로드 및 방명록 처리 완료: ${uploadGuestBookState.urls}")

                updateState { copy(isUploading = false) }
                clearFormInput()

                val isUpdate = originalState.editingGuestBookId != null
                if (isUpdate) {
                    sendEffect(MyInvitationGuestBookSideEffect.UpdateGuestBookSuccess)
                } else {
                    sendEffect(MyInvitationGuestBookSideEffect.CreateGuestBookSuccess)
                }

                // 홈 화면 새로고침 트리거
                RefreshEventHub.emit(RefreshTarget.HOME)

                // 앱 내부 저장소 파일 정리
                mediaFileCopyManager.cleanupTempFiles()

                sendEffect(MyInvitationGuestBookSideEffect.ShowSnackbar("방명록이 등록되었습니다"))
            }

            is UploadGuestBookState.Failure -> {
                Log.e("BackgroundUpload", "업로드 실패: ${uploadGuestBookState.message}")
                updateState { copy(isUploading = false) }
                sendEffect(MyInvitationGuestBookSideEffect.ShowSnackbar("업로드 실패: ${uploadGuestBookState.message}"))
            }

            is UploadGuestBookState.Cancelled -> {
                Log.d("BackgroundUpload", "업로드 취소됨")
                updateState { copy(isUploading = false) }
                sendEffect(MyInvitationGuestBookSideEffect.ShowSnackbar("업로드가 취소되었습니다"))
            }
        }
    }
}
