package com.andlife.myinvitation.screen.guestbook

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoElevation
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.domain.model.auth.AuthState
import com.andlife.media.audio.AudioPlaybackState
import com.andlife.media.video.AutoVideoPlayerPool
import com.andlife.media.video.FakeVideoPlayerPool
import com.andlife.model.common.AuthorUiModel
import com.andlife.model.common.VideoCandidate
import com.andlife.model.guestbook.GuestBookInvitationUiModel
import com.andlife.model.guestbook.GuestBookMediaUiModel
import com.andlife.model.guestbook.GuestBookUiModel
import com.andlife.model.guestbook.MediaUiType
import com.andlife.myinvitation.R
import com.andlife.myinvitation.model.guestbook.MyInvitationGuestBookSideEffect
import com.andlife.myinvitation.model.guestbook.MyInvitationGuestBookUiEvent
import com.andlife.myinvitation.model.guestbook.MyInvitationGuestBookUiState
import com.andlife.myinvitation.viewmodel.MyInvitationGuestBookViewModel
import com.andlife.ui.component.dialog.LoginDialog
import com.andlife.ui.component.AudioRecordingBottomSheet
import com.andlife.ui.component.dialog.NachoInfoDialog
import com.andlife.ui.component.dialog.NachoPermissionDialog
import com.andlife.ui.component.guestbook.GuestBookItem
import com.andlife.ui.component.invitation.InvitationGuestBookForm
import com.andlife.ui.component.paging.PagingStateContent
import com.andlife.ui.util.audio.AudioRecorder
import com.andlife.ui.util.collectWithLifecycle
import com.andlife.ui.util.imeWithoutNavBars
import com.andlife.ui.util.media.getFileSizeOrNull
import com.andlife.ui.util.media.uriToSelectedMedia
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import java.io.File
import kotlin.math.max
import kotlin.math.min

private const val CAMERA_IMAGES_DIR = "camera_images"
private const val MAX_MEDIA_SIZE_BYTES = 500 * 1024 * 1024L // 500MB
private const val MAX_MEDIAS_COUNT = 20

@Composable
fun MyInvitationGuestBookRoute(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyInvitationGuestBookViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val guestBooks = viewModel.guestBooksPagingFlow.collectAsLazyPagingItems()

    val lifecycleOwner = LocalLifecycleOwner.current
    val res = LocalResources.current
    val focusManager = LocalFocusManager.current

    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteDialog by remember { mutableStateOf<Long?>(null) }
    var showPermissionDialog by remember { mutableStateOf<String?>(null) }
    var scrollToTop by remember { mutableStateOf(false) }
    var showRecordingBottomSheet by remember { mutableStateOf(false) }

    var isMediaActive by remember { mutableStateOf(true) }
    val navigateBackWithCleanup: () -> Unit = {
        isMediaActive = false
        scope.launch {
            viewModel.videoPlayerPool.pauseAllPlayers()
            viewModel.onEvent(MyInvitationGuestBookUiEvent.ClickAudioMedia(""))

            delay(50L)
            onNavigateBack()
        }
    }
    val navigateToLoginWithCleanup: () -> Unit = {
        isMediaActive = false
        scope.launch {
            viewModel.videoPlayerPool.pauseAllPlayers()
            viewModel.onEvent(MyInvitationGuestBookUiEvent.ClickAudioMedia(""))

            delay(50L)
            onNavigateToLogin()
        }
    }


    val context = LocalContext.current
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }

    // 카메라 권한 요청 launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.onEvent(MyInvitationGuestBookUiEvent.ClickCamera)
        } else {
            showPermissionDialog = Manifest.permission.CAMERA
        }
    }

    // 카메라 촬영을 위한 launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraImageUri != null) {
            val currentMedias = uiState.selectedMedias
            if (currentMedias.size >= MAX_MEDIAS_COUNT) {
                viewModel.onEvent(MyInvitationGuestBookUiEvent.UpdateSelectedMedias(currentMedias, false, true))
            } else {
                if (getFileSizeOrNull(context, cameraImageUri!!) == null) {
                    // TODO: 파일 크기를 읽을 수 없는 경우 별도의 스낵바 안내 필요
                    viewModel.onEvent(MyInvitationGuestBookUiEvent.UpdateSelectedMedias(currentMedias, true, false))
                    // 남은 용량 계산해서 초과 여부 전달
                } else if (getFileSizeOrNull(
                        context,
                        cameraImageUri!!
                    )!! + uiState.currentMediaSizeBytes > MAX_MEDIA_SIZE_BYTES
                ) {
                    viewModel.onEvent(MyInvitationGuestBookUiEvent.UpdateSelectedMedias(currentMedias, true, false))
                } else {
                    // 촬영한 사진을 SelectedMedia로 변환해 추가
                    val newMedia = uriToSelectedMedia(context, cameraImageUri.toString())
                    val updatedMedias = (currentMedias + newMedia).toImmutableList()
                    viewModel.onEvent(MyInvitationGuestBookUiEvent.UpdateSelectedMedias(updatedMedias, false, false))
                }
            }
        }
    }

    // 오디오 권한 요청 launcher
    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.onEvent(MyInvitationGuestBookUiEvent.ClickMicrophone)
        } else {
            showPermissionDialog = Manifest.permission.RECORD_AUDIO
        }
    }

    // 오디오 녹음 객체
    val audioRecorder = remember { AudioRecorder(context) }

    viewModel.effectFlow.collectWithLifecycle { effect ->
        when (effect) {
            is MyInvitationGuestBookSideEffect.ShowSnackbar -> {
                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                        duration = SnackbarDuration.Short,
                    )
                }
            }

            is MyInvitationGuestBookSideEffect.CreateGuestBookSuccess -> {
                focusManager.clearFocus()
                scrollToTop = true
                guestBooks.refresh()
            }

            is MyInvitationGuestBookSideEffect.UpdateGuestBookSuccess -> {
                focusManager.clearFocus()
                guestBooks.refresh()
            }

            is MyInvitationGuestBookSideEffect.DeleteGuestBookSuccess -> {
                focusManager.clearFocus()
                guestBooks.refresh()
            }

            is MyInvitationGuestBookSideEffect.ScrollToTop -> {
                scope.launch {
                    if (guestBooks.itemCount > 0) {
                        lazyListState.animateScrollToItem(0)
                    }
                }
            }

            is MyInvitationGuestBookSideEffect.RefreshFailure -> {
                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(res.getString(R.string.msg_guestbook_refresh_failure))
                }
            }

            is MyInvitationGuestBookSideEffect.LaunchCamera -> {
                // 임시 파일 생성
                val cameraImagesDir = File(context.cacheDir, CAMERA_IMAGES_DIR)
                if (!cameraImagesDir.exists()) {
                    cameraImagesDir.mkdirs()
                }
                val photoFile = File(
                    cameraImagesDir,
                    "camera_photo_${System.currentTimeMillis()}.jpg"
                )
                val photoUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    photoFile
                )
                cameraImageUri = photoUri
                cameraLauncher.launch(photoUri)
            }

            is MyInvitationGuestBookSideEffect.ShowAudioRecordingBottomSheet -> {
                showRecordingBottomSheet = true
            }

            is MyInvitationGuestBookSideEffect.AuthStateChanged -> {
                guestBooks.refresh()
            }

            else -> {}
        }
    }

    LaunchedEffect(guestBooks.loadState.refresh, scrollToTop) {
        if (scrollToTop && guestBooks.loadState.refresh is LoadState.NotLoading) {
            if (guestBooks.itemCount > 0) {
                lazyListState.animateScrollToItem(0)
            }
            scrollToTop = false
        }
    }

    LaunchedEffect(guestBooks.loadState.refresh) {
        if (guestBooks.loadState.refresh !is LoadState.Loading) {
            viewModel.onRefreshFinished(
                hasError = guestBooks.loadState.refresh is LoadState.Error
            )
        }
    }

    DisposableEffect(Unit) {
        viewModel.videoPlayerPool.preparePlayers()
        onDispose {
            viewModel.videoPlayerPool.releaseAllPlayers()
            viewModel.audioPlayerManager.release()
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    viewModel.onEvent(MyInvitationGuestBookUiEvent.UpdateMediaPlayState(true))
                    viewModel.videoPlayerPool.resumeLastPlayed()
                }

                Lifecycle.Event.ON_PAUSE -> {
                    viewModel.onEvent(MyInvitationGuestBookUiEvent.UpdateMediaPlayState(false))
                    viewModel.videoPlayerPool.pauseAllPlayers()
                    viewModel.audioPlayerManager.pause()
                    if (audioRecorder.isRecording.value) {
                        audioRecorder.pauseRecording()
                    }
                }

                Lifecycle.Event.ON_DESTROY -> {
                    viewModel.videoPlayerPool.resetPool()
                    viewModel.audioPlayerManager.stopAll()
                }

                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    if (showDeleteDialog != null) {
        NachoInfoDialog(
            title = stringResource(R.string.txt_delete_dialog_title),
            message = stringResource(R.string.txt_delete_dialog_message),
            confirmText = stringResource(R.string.btn_label_delete),
            dismissText = stringResource(R.string.btn_label_cancel),
            onConfirm = {
                showDeleteDialog?.let { guestBookId ->
                    viewModel.onEvent(MyInvitationGuestBookUiEvent.ClickDeleteMenu(guestBookId))
                }
                showDeleteDialog = null
            },
            onDismiss = { showDeleteDialog = null },
        )
    }

    if (showPermissionDialog != null) {
        NachoPermissionDialog(
            message = when (showPermissionDialog) {
                Manifest.permission.CAMERA -> stringResource(R.string.txt_permission_camera)
                Manifest.permission.RECORD_AUDIO -> stringResource(R.string.txt_permission_audio)
                else -> stringResource(R.string.txt_permission_etc)
            },
            onDismiss = { showPermissionDialog = null },
        )
    }

    if (uiState.showLoginDialog) {
        LoginDialog(
            onDismiss = {
                viewModel.onEvent(MyInvitationGuestBookUiEvent.DismissLoginDialog)
            },
            onConfirm = {
                viewModel.onEvent(MyInvitationGuestBookUiEvent.DismissLoginDialog)
                navigateToLoginWithCleanup()
            }
        )
    }

    InvitationGuestBookScreen(
        uiState = uiState,
        guestBooks = guestBooks,
        onEvent = viewModel::onEvent,
        isMediaActive = isMediaActive,
        navigateBackWithCleanup = navigateBackWithCleanup,
        focusManager = focusManager,
        snackbarHostState = snackbarHostState,
        lazyListState = lazyListState,
        videoPlayerPool = viewModel.videoPlayerPool,
        onDeleteMenuClick = { guestBookId -> showDeleteDialog = guestBookId },
        context = context,
        cameraPermissionLauncher = cameraPermissionLauncher,
        audioPermissionLauncher = audioPermissionLauncher,
        modifier = modifier,
    )

    if (showRecordingBottomSheet) {
        AudioRecordingBottomSheet(
            audioRecorder = audioRecorder,
            onRecordingComplete = { recordedFile ->
                val currentMedias = uiState.selectedMedias
                if (currentMedias.size >= MAX_MEDIAS_COUNT) {
                    viewModel.onEvent(MyInvitationGuestBookUiEvent.UpdateSelectedMedias(currentMedias, false, true))
                } else {
                    // 남은 용량 계산해서 초과 여부 전달
                    if (recordedFile.length() + uiState.currentMediaSizeBytes > MAX_MEDIA_SIZE_BYTES) {
                        viewModel.onEvent(MyInvitationGuestBookUiEvent.UpdateSelectedMedias(currentMedias, true, false))
                    } else {
                        // 녹음을 SelectedMedia로 변환해 추가
                        val newMedia = uriToSelectedMedia(context, recordedFile.toURI().toString())
                        val updatedMedias = (currentMedias + newMedia).toImmutableList()
                        viewModel.onEvent(
                            MyInvitationGuestBookUiEvent.UpdateSelectedMedias(
                                updatedMedias,
                                false,
                                false
                            )
                        )
                    }
                }
                showRecordingBottomSheet = false
            },
            onDismiss = {
                showRecordingBottomSheet = false
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun InvitationGuestBookScreen(
    uiState: MyInvitationGuestBookUiState,
    guestBooks: LazyPagingItems<GuestBookUiModel>,
    onEvent: (MyInvitationGuestBookUiEvent) -> Unit,
    isMediaActive: Boolean,
    navigateBackWithCleanup: () -> Unit,
    focusManager: FocusManager,
    snackbarHostState: SnackbarHostState,
    lazyListState: LazyListState,
    videoPlayerPool: AutoVideoPlayerPool,
    onDeleteMenuClick: (Long) -> Unit,
    context: Context,
    cameraPermissionLauncher: ActivityResultLauncher<String>,
    audioPermissionLauncher: ActivityResultLauncher<String>,
    modifier: Modifier = Modifier,
) {
    val isImVisible = WindowInsets.isImeVisible

    var playVideoIndex by remember { mutableIntStateOf(-1) }
    var isTextFieldFocused by remember { mutableStateOf(false) }

    LaunchedEffect(isImVisible) {
        if (!isImVisible) focusManager.clearFocus()
    }

    BackHandler(enabled = true) {
        when {
            isImVisible -> {
                focusManager.clearFocus()
            }

            uiState.editingGuestBookId != null -> {
                focusManager.clearFocus()
                onEvent(MyInvitationGuestBookUiEvent.CancelEdit)
            }

            else -> {
                navigateBackWithCleanup()
            }
        }
    }

    LaunchedEffect(lazyListState, guestBooks.itemCount, isMediaActive, uiState.audioPlaybackState.isPlaying) {
        var pendingIndex = -1
        var lastChangedTime = 0L
        if (!isMediaActive || uiState.audioPlaybackState.isPlaying) {
            playVideoIndex = -1
            return@LaunchedEffect
        }

        snapshotFlow { lazyListState.layoutInfo }
            .collect { layoutInfo ->
                val visibleItems = layoutInfo.visibleItemsInfo
                if (visibleItems.isEmpty()) return@collect

                val videoCandidates = visibleItems.mapNotNull { itemInfo ->
                    val dataIndex = itemInfo.index

                    if (dataIndex < 0 || dataIndex >= guestBooks.itemCount) return@mapNotNull null

                    val guestBook = try {
                        guestBooks.peek(dataIndex)
                    } catch (e: Exception) {
                        null
                    }

                    val hasVideo = guestBook?.visualMedias?.any { it.type == MediaUiType.VIDEO } == true
                    if (!hasVideo) return@mapNotNull null

                    val visibleHeight = min(itemInfo.offset + itemInfo.size, layoutInfo.viewportEndOffset) -
                        max(itemInfo.offset, layoutInfo.viewportStartOffset)
                    val visibilityRatio = visibleHeight.toFloat() / itemInfo.size

                    VideoCandidate(itemInfo.index, visibilityRatio)
                }

                if (videoCandidates.isEmpty()) {
                    playVideoIndex = -1
                    return@collect
                }

                val (bestIndex, bestVisibilityRatio) = videoCandidates.maxBy { it.visibilityRatio }
                val currentPlayingItem = videoCandidates.find { it.index == playVideoIndex }
                val currentPlayingRatio = currentPlayingItem?.visibilityRatio ?: 0f

                val shouldChangeTo = when {
                    playVideoIndex != -1 && currentPlayingRatio < 0.2f -> -1
                    playVideoIndex == -1 -> if (bestVisibilityRatio >= 0.6f) bestIndex else -1
                    bestIndex != playVideoIndex && bestVisibilityRatio > currentPlayingRatio + 0.3f -> bestIndex
                    else -> playVideoIndex
                }

                if (shouldChangeTo == -1 && playVideoIndex != -1) {
                    playVideoIndex = -1
                    pendingIndex = -1
                } else if (shouldChangeTo != playVideoIndex && shouldChangeTo != pendingIndex) {
                    pendingIndex = shouldChangeTo
                    lastChangedTime = System.currentTimeMillis()
                    launch {
                        delay(200L)
                        if (pendingIndex == shouldChangeTo && System.currentTimeMillis() - lastChangedTime >= 200L) {
                            playVideoIndex = shouldChangeTo
                            pendingIndex = -1
                        }
                    }
                }
            }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = NachoTheme.colorScheme.backgroundPrimary,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        innerPadding
        Column(modifier = Modifier.fillMaxSize()) {
            PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                onRefresh = { onEvent(MyInvitationGuestBookUiEvent.Refresh) },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (isMediaActive) {
                    val isInitialLoading =
                        guestBooks.loadState.refresh is LoadState.Loading && guestBooks.itemCount == 0

                    if (isInitialLoading || guestBooks.itemCount == 0) {
                        PagingStateContent(
                            loadState = guestBooks.loadState.refresh,
                            itemCount = guestBooks.itemCount,
                            modifier = Modifier.fillMaxSize(),
                            emptyComment = stringResource(R.string.label_guestbook_empty),
                            onRetry = { guestBooks.retry() }
                        ) {}
                    } else {
                        LazyColumn(
                            state = lazyListState,
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            items(
                                count = guestBooks.itemCount,
                                key = guestBooks.itemKey { it.id }
                            ) { index ->
                                guestBooks[index]?.let { guestBook ->
                                    GuestBookItem(
                                        modifier = Modifier.animateItem(),
                                        guestBook = guestBook,
                                        videoPlayerPool = videoPlayerPool,
                                        shouldPlayVideo = uiState.canPlayVideo && (index == playVideoIndex),
                                        audioPlaybackState = uiState.audioPlaybackState,
                                        isEditing = uiState.editingGuestBookId == guestBook.id,
                                        onEditClick = { onEvent(MyInvitationGuestBookUiEvent.ClickEditMenu(guestBook)) },
                                        onDeleteClick = { onDeleteMenuClick(guestBook.id) },
                                        onVisualMediaClick = { onEvent(MyInvitationGuestBookUiEvent.ClickVisualMedia(it.url)) },
                                        onAudioMediaClick = { onEvent(MyInvitationGuestBookUiEvent.ClickAudioMedia(it.url)) },
                                        onPlayVideoClick = { url -> onEvent(MyInvitationGuestBookUiEvent.ClickVideoPlayButton(url, guestBook.id))}
                                    )
                                }
                            }

                            if (guestBooks.loadState.append is LoadState.Loading) {
                                item {
                                    Box(
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(NachoSpacing.medium),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .imeWithoutNavBars(),
                color = NachoTheme.colorScheme.backgroundPrimary,
                shadowElevation = NachoElevation.large
            ) {
                GuestBookFormSection(
                    uiState = uiState,
                    onEvent = onEvent,
                    onFocusChanged = { focused ->
                        isTextFieldFocused = focused
                    },
                    isAuthenticated = when (uiState.authState) {
                        is AuthState.Authenticated -> true
                        is AuthState.Guest -> false
                        is AuthState.Loading -> false
                    },
                    context = context,
                    cameraPermissionLauncher = cameraPermissionLauncher,
                    audioPermissionLauncher = audioPermissionLauncher
                )
            }
        }
    }
}

@Composable
private fun GuestBookFormSection(
    uiState: MyInvitationGuestBookUiState,
    onEvent: (MyInvitationGuestBookUiEvent) -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    isAuthenticated: Boolean,
    context: Context,
    cameraPermissionLauncher: ActivityResultLauncher<String>,
    audioPermissionLauncher: ActivityResultLauncher<String>,
    modifier: Modifier = Modifier,
) {
    InvitationGuestBookForm(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = NachoSpacing.large),
        selectedMedias = uiState.selectedMedias,
        textContent = uiState.textContent,
        isUploading = uiState.isUploading,
        isSubmittable = uiState.isSubmittable,
        editingGuestBookId = uiState.editingGuestBookId,
        currentMediaSizeBytes = uiState.currentMediaSizeBytes,
        onMediasSelected = { medias, exceededAvailableBytes, exceedAvailableSlots ->
            onEvent(
                MyInvitationGuestBookUiEvent.UpdateSelectedMedias(
                    medias,
                    exceededAvailableBytes,
                    exceedAvailableSlots
                )
            )
        },
        onMediaRemove = { media ->
            onEvent(MyInvitationGuestBookUiEvent.RemoveMedia(media))
        },
        onTextContentChange = { text ->
            onEvent(MyInvitationGuestBookUiEvent.UpdateTextContent(text))
        },
        onUploadClick = {
            onEvent(MyInvitationGuestBookUiEvent.UploadMedias)
        },
        isAuthenticated = isAuthenticated,
        onTextFieldClick = { onEvent(MyInvitationGuestBookUiEvent.CheckLogin) },
        onFocusChanged = onFocusChanged,
        onCameraClick = {
            // 카메라 권한 체크
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED

            if (hasPermission) {
                onEvent(MyInvitationGuestBookUiEvent.ClickCamera)
            } else {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        },
        onMicrophoneClick = {
            // 오디오 권한 체크
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED

            if (hasPermission) {
                onEvent(MyInvitationGuestBookUiEvent.ClickMicrophone)
            } else {
                audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        },
    )
}

@PreviewTheme
@Composable
private fun InvitationGuestBookEmptyPreview() {
    val emptyGuestBooks = flowOf(PagingData.empty<GuestBookUiModel>()).collectAsLazyPagingItems()
    NachoTheme {
        InvitationGuestBookScreen(
            uiState = MyInvitationGuestBookUiState(isLoadingGuestBooks = false),
            guestBooks = emptyGuestBooks,
            onEvent = {},
            isMediaActive = true,
            navigateBackWithCleanup = {},
            focusManager = LocalFocusManager.current,
            videoPlayerPool = FakeVideoPlayerPool(),
            snackbarHostState = SnackbarHostState(),
            lazyListState = rememberLazyListState(),
            onDeleteMenuClick = {},
            context = LocalContext.current,
            cameraPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) {},
            audioPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) {},
        )
    }
}

@PreviewTheme
@Composable
private fun InvitationGuestBookResultPreview() {
    val fakeGuestBooks = listOf(
        GuestBookUiModel(
            id = 1L,
            invitation = GuestBookInvitationUiModel(id = 222L, title = "우리 결혼해요!"),
            author = AuthorUiModel(id = 111L, name = "홍길동", profileImageUrl = null),
            textContent = "결혼 축하드려요! 행복하게 잘 사세요~!",
            visualMedias = emptyList<GuestBookMediaUiModel>().toImmutableList(),
            audioMedias = listOf(
                GuestBookMediaUiModel(
                    id = 10L,
                    type = MediaUiType.AUDIO,
                    url = "https://example.com/audio.m4a",
                    durationSeconds = 15,
                    displayOrder = 1
                )
            ).toImmutableList(),
            totalVisualCount = 0,
            isOwner = true,
            isInvitationOwner = false,
            createdAt = LocalDateTime(2026, 1, 20, 10, 0),
            updatedAt = LocalDateTime(2026, 1, 20, 10, 0),
        ),
        GuestBookUiModel(
            id = 2L,
            author = AuthorUiModel(id = 112L, name = "이순신", profileImageUrl = null),
            textContent = "직접 가서 축하해주고 싶었는데 아쉽네요. 멀리서나마 응원합니다!",
            visualMedias = emptyList<GuestBookMediaUiModel>().toImmutableList(),
            audioMedias = emptyList<GuestBookMediaUiModel>().toImmutableList(),
            totalVisualCount = 0,
            isOwner = false,
            isInvitationOwner = false,
            createdAt = LocalDateTime(2026, 1, 19, 15, 30),
            updatedAt = LocalDateTime(2026, 1, 19, 15, 30),
        )
    )

    NachoTheme {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(NachoSpacing.large),
            verticalArrangement = Arrangement.spacedBy(NachoSpacing.large)
        ) {
            items(fakeGuestBooks.size) { index ->
                GuestBookItem(
                    guestBook = fakeGuestBooks[index],
                    videoPlayerPool = FakeVideoPlayerPool(),
                    shouldPlayVideo = false,
                    audioPlaybackState = AudioPlaybackState(),
                    onInvitationTitleClick = {},
                    onVisualMediaClick = {},
                    onAudioMediaClick = {},
                    onPlayVideoClick = {}
                )
            }
        }
    }
}
