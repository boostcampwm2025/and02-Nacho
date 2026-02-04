package com.andlife.invitation.screen.collection

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.domain.model.guestbook.MediaType
import com.andlife.invitation.R
import com.andlife.invitation.model.collection.InvitationCollectionSideEffect
import com.andlife.invitation.model.collection.InvitationCollectionUiEvent
import com.andlife.invitation.model.collection.InvitationCollectionUiState
import com.andlife.invitation.viewmodel.InvitationCollectionViewModel
import com.andlife.model.collection.CollectionUiModel
import com.andlife.model.guestbook.UiMediaType
import com.andlife.model.util.toUiType
import com.andlife.ui.component.collection.StoryContent
import com.andlife.ui.component.collection.StoryTopHeader
import com.andlife.ui.component.dialog.NachoInfoDialog
import com.andlife.ui.component.dialog.NachoPermissionDialog
import com.andlife.ui.util.shouldRequestStoragePermission
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun InvitationStoryRoute(
    initialIndex: Int,
    onPageChanged: (Int) -> Unit,
    onToggleExpand: () -> Unit,
    onClose: () -> Unit,
    viewModel: InvitationCollectionViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val res = LocalResources.current


    var showPermissionDeniedDialog by remember { mutableStateOf(false) }
    var showNetworkInfoDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.onEvent(InvitationCollectionUiEvent.DownloadMedia)
        } else {
            showPermissionDeniedDialog = true
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effectFlow.collect { effect ->
            when (effect) {
                is InvitationCollectionSideEffect.DownloadFailed -> {
                    scope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarHostState.showSnackbar(res.getString(R.string.snack_download_fail))
                    }
                }

                is InvitationCollectionSideEffect.ShowDownloadGuide -> {
                    scope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarHostState.showSnackbar(res.getString(R.string.snack_download_guide))
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.playerPool.releaseAll()
        }
    }

    InvitationStoryScreen(
        uiState = uiState,
        getPlayerForIndex = { index -> viewModel.getPlayerForIndex(index) },
        initialIndex = initialIndex,
        onPageChanged = onPageChanged,
        onToggleExpand = onToggleExpand,
        onClose = onClose,
        onDownloadClick = {
            if (context.shouldRequestStoragePermission()) {
                permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            } else if (!uiState.networkDialogDismissed) {
                showNetworkInfoDialog = true
            } else {
                viewModel.onEvent(InvitationCollectionUiEvent.DownloadMedia)
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
            onDoNotShowAgainChecked = { viewModel.onEvent(InvitationCollectionUiEvent.DisableNetworkDialogPermanently) },
            onConfirm = {
                showNetworkInfoDialog = false
                viewModel.onEvent(InvitationCollectionUiEvent.DownloadMedia)
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
}

@Composable
fun InvitationStoryScreen(
    uiState: InvitationCollectionUiState,
    getPlayerForIndex: (Int) -> Player?,
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

    // 다음 페이지가 이미지라면 미리 로딩
    val beyondViewportPageCount = remember(pagerState.currentPage, uiState.mediaItems) {
        val nextIndex = pagerState.currentPage + 1
        if (nextIndex in uiState.mediaItems.indices) {
            val nextItem = uiState.mediaItems[nextIndex]
            if (nextItem.type == UiMediaType.IMAGE) 1 else 0
        } else 0
    }

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
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
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
                beyondViewportPageCount = beyondViewportPageCount,
            ) { pageIndex ->
                val item = uiState.mediaItems[pageIndex]
                val isCurrentPage = pagerState.currentPage == pageIndex
                val currentPlayer = if (isCurrentPage) getPlayerForIndex(pageIndex) else null

                Box(modifier = Modifier.fillMaxSize()) {
                    StoryContent(
                        item = item,
                        isExpanded = uiState.isTextExpanded,
                        onToggleExpand = onToggleExpand,
                        exoPlayer = currentPlayer,
                        modifier = Modifier.align(Alignment.BottomCenter),
                    )
                }
            }
        }
    }
}


@PreviewTheme
@Composable
private fun InvitationStoryScreenPreview() {
    NachoTheme {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val mockState = InvitationCollectionUiState(
            mediaItems = persistentListOf(
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
            ),
            isTextExpanded = false,
        )

        val context = LocalContext.current
        val dummyPlayer = remember { ExoPlayer.Builder(context).build() }

        InvitationStoryScreen(
            uiState = mockState,
            getPlayerForIndex = { index -> dummyPlayer },
            initialIndex = 0,
            onPageChanged = {},
            onToggleExpand = {},
            onClose = {},
            onDownloadClick = {},
            snackbarHostState = remember { SnackbarHostState() },
        )
    }
}


