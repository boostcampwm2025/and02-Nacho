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
import com.andlife.domain.model.guestbook.UploadState
import com.andlife.domain.repository.auth.AuthStateManager
import com.andlife.domain.repository.guestbook.GuestBookRepository
import com.andlife.domain.util.BackgroundMediaUploader
import com.andlife.domain.util.RefreshEventHub
import com.andlife.domain.util.RefreshEventHub.RefreshTarget
import com.andlife.domain.util.Result
import com.andlife.domain.util.onFailure
import com.andlife.domain.util.onSuccess
import com.andlife.media.audio.AudioPlaybackState
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
    private val backgroundMediaUploader: BackgroundMediaUploader,
    private val guestBookRepository: GuestBookRepository,
    private val authStateManager: AuthStateManager,
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
                event.exceededAvailableBytes,
                event.exceededAvailableSlots
            )

            is MyInvitationGuestBookUiEvent.UpdateTextContent -> updateTextContent(event.textContent)
            is MyInvitationGuestBookUiEvent.RemoveMedia -> removeMedia(event.media)
            is MyInvitationGuestBookUiEvent.UploadMedias -> handleUploadMedias()
            is MyInvitationGuestBookUiEvent.ClickCamera -> handleCameraClick()
            is MyInvitationGuestBookUiEvent.ClickMicrophone -> handleMicrophoneClick()
            is MyInvitationGuestBookUiEvent.ClearError -> clearError()
            is MyInvitationGuestBookUiEvent.ClickAudioMedia -> clickAudioMedia(event.url)
            is MyInvitationGuestBookUiEvent.ClickVideoPlayButton -> clickVideoPlayButton(event.url, event.itemId)
            is MyInvitationGuestBookUiEvent.ClickVisualMedia -> {}
            is MyInvitationGuestBookUiEvent.ClickEditMenu -> startEditing(event.guestBook)
            is MyInvitationGuestBookUiEvent.CancelEdit -> cancelEdit()
            is MyInvitationGuestBookUiEvent.ClickDeleteMenu -> deleteGuestBook(event.guestBookId)
            is MyInvitationGuestBookUiEvent.UpdateMediaPlayState -> updatePlayState(event.isPlaying)
            MyInvitationGuestBookUiEvent.Refresh -> refresh()
            MyInvitationGuestBookUiEvent.CheckLogin -> checkLogin()
            MyInvitationGuestBookUiEvent.DismissLoginDialog -> dismissLoginDialog()
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
        exceededAvailableBytes: Boolean,
        exceededAvailableSlots: Boolean
    ) {
        updateState {
            copy(
                selectedMedias = medias.toPersistentList(),
                currentMediaSizeBytes = calculateTotalMediaSize(medias)
            )
        }

        // 용량 초과로 거부된 파일이 있으면 스낵바로 알림
        if (exceededAvailableBytes) {
            sendEffect(
                MyInvitationGuestBookSideEffect.ShowSnackbar(
                    "파일이 500MB를 초과하여 제외되었습니다."
                )
            )
        }

        // 제외된 파일이 있으면 스낵바로 알림 (후순위)
        else if (exceededAvailableSlots) {
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

        updateState { copy(isUploading = true) }

        val newMedias = state.selectedMedias.filter { it.id == null }

        // 백그라운드 업로드 시작
        val workId = backgroundMediaUploader.uploadMediasInBackground(
            newMedias.map { it.uri },
            invitationId = invitationId.toString(),
            guestBookText = state.textContent,
            isEditing = state.editingGuestBookId != null,
            editingGuestBookId = state.editingGuestBookId?.toString()
        )

        val logMessage = if (newMedias.isNotEmpty()) {
            "백그라운드 업로드 시작 (미디어 ${newMedias.size}개)"
        } else {
            "백그라운드 방명록 처리 시작 (미디어 없음)"
        }
        Log.d("BackgroundUpload", "$logMessage - WorkID: $workId")

        // 업로드 진행상황 관찰
        viewModelScope.launch {
            backgroundMediaUploader.observeUploadProgress(workId).collect { uploadState ->
                handleUploadStateChange(uploadState, state)
            }
        }
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
        updateState { copy(audioRecordingDuration = 0) }
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


    private fun handleUploadStateChange(
        uploadState: UploadState,
        originalState: MyInvitationGuestBookUiState
    ) {
        Log.d("BackgroundUpload", "업로드 상태 변화: $uploadState")

        when (uploadState) {
            is UploadState.Enqueued -> {
                Log.d("BackgroundUpload", "업로드 대기 중")
            }

            is UploadState.Progress -> {
                Log.d(
                    "BackgroundUpload",
                    "업로드 진행: ${uploadState.percent}% (${uploadState.currentIndex + 1}/${uploadState.totalFiles})"
                )
            }

            is UploadState.Success -> {
                Log.d("BackgroundUpload", "업로드 및 방명록 처리 완료: ${uploadState.urls}")

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

                sendEffect(MyInvitationGuestBookSideEffect.ShowSnackbar("방명록이 등록되었습니다"))
            }

            is UploadState.Failure -> {
                Log.e("BackgroundUpload", "업로드 실패: ${uploadState.message}")
                updateState { copy(isUploading = false) }
                sendEffect(MyInvitationGuestBookSideEffect.ShowSnackbar("업로드 실패: ${uploadState.message}"))
            }

            is UploadState.Cancelled -> {
                Log.d("BackgroundUpload", "업로드 취소됨")
                updateState { copy(isUploading = false) }
                sendEffect(MyInvitationGuestBookSideEffect.ShowSnackbar("업로드가 취소되었습니다"))
            }
        }
    }
}
