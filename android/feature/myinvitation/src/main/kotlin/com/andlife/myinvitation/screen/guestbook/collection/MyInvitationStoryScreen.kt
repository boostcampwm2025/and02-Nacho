package com.andlife.myinvitation.screen.guestbook.collection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.domain.model.guestbook.MediaType
import com.andlife.model.invitation.collection.CollectionUiModel
import com.andlife.model.util.toUiType
import com.andlife.myinvitation.model.guestbook.collection.MyInvitationCollectionUiState
import com.andlife.myinvitation.viewmodel.MyInvitationCollectionViewModel
import com.andlife.ui.component.invitation.collection.StoryContent
import com.andlife.ui.component.invitation.collection.StoryTopHeader
import kotlinx.collections.immutable.persistentListOf
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

    InvitationStoryScreen(
        uiState = uiState,
        exoPlayer = viewModel.exoPlayer,
        initialIndex = initialIndex,
        onPageChanged = onPageChanged,
        onToggleExpand = onToggleExpand,
        onClose = onClose,
    )
}

@Composable
fun InvitationStoryScreen(
    uiState: MyInvitationCollectionUiState,
    exoPlayer: Player,
    initialIndex: Int,
    onPageChanged: (Int) -> Unit,
    onToggleExpand: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState =
        rememberPagerState(
            initialPage = initialIndex,
            pageCount = { uiState.mediaItems.size },
        )

    LaunchedEffect(pagerState.currentPage) {
        onPageChanged(pagerState.currentPage)
    }

    val currentItem = uiState.mediaItems.getOrNull(pagerState.currentPage)

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
            onClose = {},
        )
    }
}
