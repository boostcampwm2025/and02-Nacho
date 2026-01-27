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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
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
import com.andlife.designsystem.component.dialog.NachoDialog
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoElevation
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
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
import com.andlife.ui.component.guestbook.GuestBookItem
import com.andlife.ui.component.invitation.InvitationGuestBookForm
import com.andlife.ui.component.paging.PagingStateContent
import com.andlife.ui.util.audio.AudioRecorder
import com.andlife.ui.util.collectWithLifecycle
import com.andlife.ui.util.media.uriToSelectedMedia
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import java.io.File
import kotlin.math.max
import kotlin.math.min
import kotlin.toString

private const val CAMERA_IMAGES_DIR = "camera_images"
private const val AUDIO_RECORDINGS_DIR = "audio_recordings"

@Composable
fun MyInvitationGuestBookRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyInvitationGuestBookViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val guestBooks = viewModel.guestBooksPagingFlow.collectAsLazyPagingItems()

    val lifecycleOwner = LocalLifecycleOwner.current
    val lazyListState = rememberLazyListState()

    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteDialog by remember { mutableStateOf<Long?>(null) }
    var scrollToTop by remember { mutableStateOf(false) }


    val context = LocalContext.current

    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }
    val audioRecorder = remember { AudioRecorder(context) }

    // 카메라 권한 요청 launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.onEvent(MyInvitationGuestBookUiEvent.ClickCamera)
        }
    }

    // 카메라 촬영을 위한 launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraImageUri != null) {
            val currentMedias = uiState.selectedMedias
            if (currentMedias.size < 5) {
                // 촬영한 사진을 SelectedMedia로 변환하여 추가
                val newMedia = uriToSelectedMedia(context, cameraImageUri.toString())
                val updatedMedias = (currentMedias + newMedia).toImmutableList()
                viewModel.onEvent(MyInvitationGuestBookUiEvent.UpdateSelectedMedias(updatedMedias))
            }
        }
    }

    // 오디오 권한 요청 launcher
    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.onEvent(MyInvitationGuestBookUiEvent.ClickMicrophone)
        }
    }

    viewModel.effectFlow.collectWithLifecycle { effect ->
        when (effect) {
            is MyInvitationGuestBookSideEffect.ShowSnackbar -> {
                snackbarHostState.showSnackbar(
                    message = effect.message,
                    duration = SnackbarDuration.Short,
                )
            }

            is MyInvitationGuestBookSideEffect.CreateGuestBookSuccess -> {
                scrollToTop = true
                viewModel.invalidateGuestBooks()
            }

            is MyInvitationGuestBookSideEffect.UpdateGuestBookSuccess -> {
                viewModel.invalidateGuestBooks()
            }

            is MyInvitationGuestBookSideEffect.DeleteGuestBookSuccess -> {
                viewModel.videoPlayerPool.clearCacheById(uiState.editingGuestBookId)
                viewModel.invalidateGuestBooks()
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
                cameraLauncher.launch(photoUri)
            }

            is MyInvitationGuestBookSideEffect.StartAudioRecording -> {
                // 오디오 녹음 시작
                val audioRecordingsDir = File(context.cacheDir, AUDIO_RECORDINGS_DIR)
                if (!audioRecordingsDir.exists()) {
                    audioRecordingsDir.mkdirs()
                }
                val audioFile = File(
                    audioRecordingsDir,
                    "audio_${System.currentTimeMillis()}.m4a"
                )

                audioRecorder.startRecording(audioFile) { e ->
                    viewModel.onEvent(MyInvitationGuestBookUiEvent.StopAudioRecording)
                    // exception 표시
                }
            }

            is MyInvitationGuestBookSideEffect.StopAudioRecording -> {
                // 오디오 녹음 중지
                audioRecorder.stopRecording { recordedFile ->
                    if (recordedFile != null) {
                        // 녹음된 오디오 파일을 SelectedMedia로 변환
                        val currentMedias = uiState.selectedMedias
                        if (currentMedias.size < 5) {
                            val audioMedia = uriToSelectedMedia(context, recordedFile.toURI().toString())
                            val updatedMedias = (currentMedias + audioMedia).toImmutableList()
                            viewModel.onEvent(MyInvitationGuestBookUiEvent.UpdateSelectedMedias(updatedMedias))
                        }
                    }
                }
            }
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
                    viewModel.videoPlayerPool.resumeLastPlayed()
                }

                Lifecycle.Event.ON_PAUSE -> {
                    viewModel.videoPlayerPool.pauseAllPlayers()
                    viewModel.audioPlayerManager.pause()
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
        NachoDialog(
            onDismiss = { showDeleteDialog = null }
        ) {
            Column(
                modifier = Modifier.padding(NachoSpacing.xLarge),
                verticalArrangement = Arrangement.spacedBy(NachoSpacing.medium)
            ) {
                Text(
                    text = stringResource(R.string.txt_delete_dialog_title),
                    color = NachoTheme.colorScheme.textPrimary,
                    style = NachoTheme.typography.headingSmallSemiBold,
                )
                Spacer(modifier = Modifier.padding(NachoSpacing.xSmall))
                Text(
                    text = stringResource(R.string.txt_delete_dialog_message),
                    color = NachoTheme.colorScheme.textSecondary,
                    style = NachoTheme.typography.bodyMediumRegular,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = { showDeleteDialog = null }
                    ) {
                        Text(
                            text = stringResource(R.string.btn_label_cancel),
                            color = NachoTheme.colorScheme.textPrimary,
                            style = NachoTheme.typography.bodyMediumSemiBold,
                        )
                    }
                    TextButton(
                        onClick = {
                            showDeleteDialog?.let { guestBookId ->
                                viewModel.onEvent(MyInvitationGuestBookUiEvent.ClickDeleteMenu(guestBookId))
                            }
                            showDeleteDialog = null
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.btn_label_delete),
                            color = NachoTheme.colorScheme.brandDark,
                            style = NachoTheme.typography.bodyMediumSemiBold,
                        )
                    }
                }
            }
        }
    }

    InvitationGuestBookScreen(
        uiState = uiState,
        guestBooks = guestBooks,
        onEvent = viewModel::onEvent,
        onNavigateBack = onNavigateBack,
        snackbarHostState = snackbarHostState,
        lazyListState = lazyListState,
        videoPlayerPool = viewModel.videoPlayerPool,
        onDeleteMenuClick = { guestBookId -> showDeleteDialog = guestBookId },
        context = context,
        cameraPermissionLauncher = cameraPermissionLauncher,
        audioPermissionLauncher = audioPermissionLauncher,
        modifier = modifier,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun InvitationGuestBookScreen(
    uiState: MyInvitationGuestBookUiState,
    guestBooks: LazyPagingItems<GuestBookUiModel>,
    onEvent: (MyInvitationGuestBookUiEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    lazyListState: LazyListState,
    videoPlayerPool: AutoVideoPlayerPool,
    onNavigateBack: () -> Unit,
    onDeleteMenuClick: (Long) -> Unit,
    context: Context,
    cameraPermissionLauncher: ActivityResultLauncher<String>,
    audioPermissionLauncher: ActivityResultLauncher<String>,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val isImVisible = WindowInsets.isImeVisible

    var playVideoIndex by remember { mutableIntStateOf(-1) }
    var isMediaActive by remember { mutableStateOf(true) }
    var isTextFieldFocused by remember { mutableStateOf(false) }

    val navigateBackWithCleanup: () -> Unit = {
        isMediaActive = false
        coroutineScope.launch {
            videoPlayerPool.pauseAllPlayers()
            onEvent(MyInvitationGuestBookUiEvent.ClickAudioMedia(""))

            delay(50L)
            onNavigateBack()
        }
    }

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

    LaunchedEffect(lazyListState, guestBooks.itemCount, isMediaActive, uiState.isAudioPlaying) {
        var pendingIndex = -1
        var lastChangedTime = 0L
        if (!isMediaActive || uiState.isAudioPlaying) {
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
        bottomBar = {
            Surface(
                tonalElevation = NachoElevation.medium,
                shadowElevation = NachoElevation.large,
                color = NachoTheme.colorScheme.backgroundPrimary
            ) {
                Box(modifier = Modifier.imePadding()) {
                    GuestBookFormSection(
                        uiState = uiState,
                        onEvent = onEvent,
                        onFocusChanged = { focused ->
                            isTextFieldFocused = focused
                        },
                        context = context,
                        cameraPermissionLauncher = cameraPermissionLauncher,
                        audioPermissionLauncher = audioPermissionLauncher
                    )
                }
            }
        }
    ) { innerPadding ->
        if (isMediaActive) {
            val isInitialLoading = guestBooks.loadState.refresh is LoadState.Loading && guestBooks.itemCount == 0

            if (isInitialLoading || guestBooks.itemCount == 0) {
                PagingStateContent(
                    loadState = guestBooks.loadState.refresh,
                    itemCount = guestBooks.itemCount,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = innerPadding.calculateBottomPadding()),
                    onRetry = { guestBooks.retry() }
                ) {}
            } else {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        bottom = innerPadding.calculateBottomPadding()
                    )
                ) {
                    items(
                        count = guestBooks.itemCount,
                        key = guestBooks.itemKey { it.id }
                    ) { index ->
                        guestBooks[index]?.let { guestBook ->
                            GuestBookItem(
                                modifier = Modifier
                                    .animateItem(),
                                guestBook = guestBook,
                                videoPlayerPool = videoPlayerPool,
                                shouldPlayVideo = isMediaActive && (index == playVideoIndex),
                                isAudioPlaying = uiState.isAudioPlaying &&
                                    guestBook.audioMedias.any { it.url == uiState.playingAudioUrl },
                                playingAudioUrl = uiState.playingAudioUrl,
                                isEditing = uiState.editingGuestBookId == guestBook.id,
                                onEditClick = { onEvent(MyInvitationGuestBookUiEvent.ClickEditMenu(guestBook)) },
                                onDeleteClick = { onDeleteMenuClick(guestBook.id) },
                                onVisualMediaClick = { onEvent(MyInvitationGuestBookUiEvent.ClickVisualMedia(it.url)) },
                                onAudioMediaClick = { onEvent(MyInvitationGuestBookUiEvent.ClickAudioMedia(it.url)) },
                                onMenuClick = { onEvent(MyInvitationGuestBookUiEvent.ClickGuestBookMenu(guestBook.id)) },
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
}

@Composable
private fun GuestBookFormSection(
    uiState: MyInvitationGuestBookUiState,
    onEvent: (MyInvitationGuestBookUiEvent) -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    context: Context,
    cameraPermissionLauncher: ActivityResultLauncher<String>,
    audioPermissionLauncher: ActivityResultLauncher<String>,
) {
    InvitationGuestBookForm(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = NachoSpacing.large,
                vertical = NachoSpacing.xSmall,
            ),
        selectedMedias = uiState.selectedMedias,
        textContent = uiState.textContent,
        isUploading = uiState.isUploading,
        isSubmittable = uiState.isSubmittable,
        editingGuestBookId = uiState.editingGuestBookId,
        isAudioRecording = uiState.isAudioRecording,
        onMediasSelected = { medias ->
            onEvent(MyInvitationGuestBookUiEvent.UpdateSelectedMedias(medias))
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
                if (uiState.isAudioRecording) {
                    onEvent(MyInvitationGuestBookUiEvent.StopAudioRecording)
                } else {
                    onEvent(MyInvitationGuestBookUiEvent.StartAudioRecording)
                }
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
            onNavigateBack = {},
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
                    isAudioPlaying = false,
                    playingAudioUrl = null,
                    onInvitationTitleClick = {},
                    onVisualMediaClick = {},
                    onAudioMediaClick = {},
                    onMenuClick = {},
                )
            }
        }
    }
}
