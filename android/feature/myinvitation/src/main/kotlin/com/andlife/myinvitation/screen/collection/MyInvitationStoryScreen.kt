package com.andlife.myinvitation.screen.collection

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.domain.model.guestbook.MediaType
import com.andlife.model.collection.CollectionUiModel
import com.andlife.model.util.toUiType
import com.andlife.myinvitation.R
import com.andlife.myinvitation.model.collection.MyInvitationCollectionSideEffect
import com.andlife.myinvitation.model.collection.MyInvitationCollectionUiEvent
import com.andlife.myinvitation.model.collection.MyInvitationCollectionUiState
import com.andlife.myinvitation.viewmodel.MyInvitationCollectionViewModel
import com.andlife.ui.component.collection.StoryContent
import com.andlife.ui.component.collection.StoryTopHeader
import com.andlife.ui.component.dialog.NachoInfoDialog
import com.andlife.ui.component.dialog.NachoPermissionDialog
import com.andlife.ui.util.shouldRequestNotificationPermission
import com.andlife.ui.util.shouldRequestStoragePermission
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun MyInvitationStoryRoute(
    initialIndex: Int,
    onPageChanged: (Int) -> Unit,
    onToggleExpand: () -> Unit,
    onClose: () -> Unit,
    viewModel: MyInvitationCollectionViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var showPermissionDeniedDialog by remember { mutableStateOf(false) }
    var showNotificationPermissionDeniedDialog by remember { mutableStateOf(false) }
    var showNetworkInfoDialog by remember { mutableStateOf(false) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            showNotificationPermissionDeniedDialog = true
        } else if (!uiState.networkDialogDismissed) {
            showNetworkInfoDialog = true
        } else {
            viewModel.onEvent(MyInvitationCollectionUiEvent.DownloadMedia)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            if (context.shouldRequestNotificationPermission()) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else if (!uiState.networkDialogDismissed) {
                showNetworkInfoDialog = true
            } else {
                viewModel.onEvent(MyInvitationCollectionUiEvent.DownloadMedia)
            }
        } else {
            showPermissionDeniedDialog = true
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effectFlow.collect { effect ->
            when (effect) {
                is MyInvitationCollectionSideEffect.DownloadFailed -> {
                    scope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarHostState.showSnackbar(context.getString(R.string.snack_download_fail))
                    }
                }

                is MyInvitationCollectionSideEffect.ShowDownloadGuide -> {
                    scope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarHostState.showSnackbar(context.getString(R.string.snack_download_guide))
                    }
                }
            }
        }
    }

    InvitationStoryScreen(
        uiState = uiState,
        exoPlayer = viewModel.exoPlayer,
        initialIndex = initialIndex,
        onPageChanged = onPageChanged,
        onToggleExpand = onToggleExpand,
        onClose = onClose,
        onDownloadClick = {
            if (context.shouldRequestStoragePermission()) {
                permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            } else if (context.shouldRequestNotificationPermission()) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else if (!uiState.networkDialogDismissed) {
                showNetworkInfoDialog = true
            } else {
                viewModel.onEvent(MyInvitationCollectionUiEvent.DownloadMedia)
            }
        },
        snackbarHostState = snackbarHostState,
    )

    if (showNetworkInfoDialog) {
        NachoInfoDialog(
            title = stringResource(R.string.dialog_network_title),
            message = stringResource(R.string.dialog_network_message),
            confirmText = stringResource(R.string.dialog_confirm),
            dismissText = stringResource(R.string.dialog_cancel),
            showDoNotShowAgain = true,
            onDoNotShowAgainChecked = { viewModel.onEvent(MyInvitationCollectionUiEvent.DisableNetworkDialogPermanently) },
            onConfirm = {
                showNetworkInfoDialog = false
                viewModel.onEvent(MyInvitationCollectionUiEvent.DownloadMedia)
            },
            onDismiss = { showNetworkInfoDialog = false },
        )
    }

    if (showPermissionDeniedDialog) {
        NachoPermissionDialog(
            message = stringResource(R.string.snack_permission_denied),
            onDismiss = { showPermissionDeniedDialog = false },
        )
    }

    if (showNotificationPermissionDeniedDialog) {
        NachoPermissionDialog(
            message = stringResource(R.string.snack_notification_permission_denied),
            onDismiss = {
                showNotificationPermissionDeniedDialog = false
                if (!uiState.networkDialogDismissed) {
                    showNetworkInfoDialog = true
                } else {
                    viewModel.onEvent(MyInvitationCollectionUiEvent.DownloadMedia)
                }
            },
        )
    }
}

@Composable
fun InvitationStoryScreen(
    uiState: MyInvitationCollectionUiState,
    exoPlayer: Player,
    initialIndex: Int,
    onPageChanged: (Int) -> Unit,
    onToggleExpand: () -> Unit,
    onClose: () -> Unit,
    onDownloadClick: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val pagerState =
        rememberPagerState(
            initialPage = initialIndex,
            pageCount = { uiState.mediaItems.size },
        )

    val currentItem = uiState.mediaItems.getOrNull(pagerState.currentPage)
    val isDownloading = currentItem?.let {
        uiState.downloadingUrls.contains(it.mediaUrl)
    } ?: false

    LaunchedEffect(pagerState.currentPage) {
        onPageChanged(pagerState.currentPage)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = NachoTheme.colorScheme.backgroundInverse,
        modifier = modifier,
    ) { paddingValues ->
        Column(
            modifier =
                modifier
                    .fillMaxSize()
                    .background(NachoTheme.colorScheme.backgroundInverse),
        ) {
            currentItem?.let { item ->
                StoryTopHeader(
                    name = item.authorName,
                    date = item.createdAt,
                    profileUrl = item.authorProfileUrl,
                    onClose = onClose,
                    onDownloadClick = onDownloadClick,
                    isDownloading = isDownloading,
                )
            }

            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                pageSpacing = NachoSpacing.none,
                userScrollEnabled = true,
            ) { pageIndex ->
                val item = uiState.mediaItems[pageIndex]
                val isCurrentPage = pagerState.currentPage == pageIndex

                Box(modifier = Modifier.fillMaxSize()) {
                    StoryContent(
                        item = item,
                        isExpanded = uiState.isTextExpanded,
                        onToggleExpand = onToggleExpand,
                        exoPlayer = if (isCurrentPage) exoPlayer else null,
                        modifier = Modifier.align(Alignment.BottomCenter),
                    )
                }
            }
        }
    }
}

@PreviewTheme
@Composable
private fun MyInvitationStoryScreenPreview() {
    NachoTheme {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val mockState =
            MyInvitationCollectionUiState(
                mediaItems =
                    persistentListOf(
                        CollectionUiModel(
                            id = 1L,
                            mediaUrl = "https://picsum.photos/400/600?random=1",
                            type = MediaType.IMAGE.toUiType(),
                            content = "방명록 내용 1",
                            authorName = "사용자1",
                            authorProfileUrl = null,
                            createdAt = now,
                            durationSeconds = null,
                        ),
                        CollectionUiModel(
                            id = 2L,
                            mediaUrl = "https://picsum.photos/400/600?random=2",
                            type = MediaType.VIDEO.toUiType(),
                            content = "방명록 내용 2",
                            authorName = "사용자2",
                            authorProfileUrl = null,
                            createdAt = now,
                            durationSeconds = 120,
                        ),
                        CollectionUiModel(
                            id = 3L,
                            mediaUrl = "https://picsum.photos/400/600?random=3",
                            type = MediaType.AUDIO.toUiType(),
                            content = "방명록 내용 3",
                            authorName = "사용자3",
                            authorProfileUrl = null,
                            createdAt = now,
                            durationSeconds = 300,
                        ),
                    ),
                isTextExpanded = false,
            )

        val context = LocalContext.current
        val dummyPlayer = remember {
            ExoPlayer.Builder(context).build()
        }

        InvitationStoryScreen(
            uiState = mockState,
            initialIndex = 0,
            onPageChanged = {},
            onToggleExpand = {},
            exoPlayer = dummyPlayer,
            onDownloadClick = {},
            onClose = {},
            snackbarHostState = SnackbarHostState(),
        )
    }
}
