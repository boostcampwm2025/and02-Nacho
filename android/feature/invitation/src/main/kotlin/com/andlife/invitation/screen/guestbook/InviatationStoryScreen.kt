package com.andlife.invitation.screen.guestbook

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.InvitationSpacing
import com.andlife.designsystem.theme.InvitationTheme
import com.andlife.domain.model.guestbook.MediaType
import com.andlife.invitation.component.StoryContent
import com.andlife.invitation.component.StoryTopHeader
import com.andlife.invitation.model.guestbook.collection.InvitationCollectionUiEvent
import com.andlife.invitation.model.guestbook.collection.InvitationCollectionUiModel
import com.andlife.invitation.model.guestbook.collection.InvitationCollectionUiState
import com.andlife.invitation.util.toUiType
import com.andlife.invitation.viewmodel.InvitationCollectionViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun InvitationStoryRoute(
    viewModel: InvitationCollectionViewModel,
    initialIndex: Int,
    onClose: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    InvitationStoryScreen(
        uiState = uiState,
        initialIndex = initialIndex,
        onPageChanged = { index ->
            viewModel.onEvent(InvitationCollectionUiEvent.PageChanged(index))
        },
        onToggleExpand = {
            viewModel.onEvent(InvitationCollectionUiEvent.ToggleExpand)
        },
        onClose = onClose
    )
}

@Composable
fun InvitationStoryScreen(
    uiState: InvitationCollectionUiState,
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
                .background(InvitationTheme.colorScheme.backgroundInverse),
    ) {
        currentItem?.let { item ->
            StoryTopHeader(
                name = item.authorName,
                date = item.createdAt,
                profileUrl = item.authorProfileUrl,
                onClose = onClose,
            )
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            pageSpacing = InvitationSpacing.none,
            userScrollEnabled = true,
        ) { pageIndex ->
            val item = uiState.mediaItems[pageIndex]

            Box(modifier = Modifier.fillMaxSize()) {
                StoryContent(
                    item = item,
                    isExpanded = uiState.isTextExpanded,
                    onToggleExpand = onToggleExpand,
                    modifier = Modifier.align(Alignment.BottomCenter),
                )
            }
        }
    }
}

@PreviewTheme
@Composable
private fun InvitationStoryScreenPreview() {
    InvitationTheme {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val mockState = InvitationCollectionUiState(
            mediaItems = persistentListOf(
                InvitationCollectionUiModel(
                    id = 1L,
                    url = "https://picsum.photos/400/600?random=1",
                    type = MediaType.IMAGE.toUiType(),
                    content = "방명록 내용 1",
                    authorName = "사용자1",
                    authorProfileUrl = null,
                    createdAt = now,
                    durationSeconds = null,
                ),
                InvitationCollectionUiModel(
                    id = 2L,
                    url = "https://picsum.photos/400/600?random=2",
                    type = MediaType.VIDEO.toUiType(),
                    content = "방명록 내용 2",
                    authorName = "사용자2",
                    authorProfileUrl = null,
                    createdAt = now,
                    durationSeconds = 120,
                ),
                InvitationCollectionUiModel(
                    id = 3L,
                    url = "https://picsum.photos/400/600?random=3",
                    type = MediaType.AUDIO.toUiType(),
                    content = "방명록 내용 3",
                    authorName = "사용자3",
                    authorProfileUrl = null,
                    createdAt = now,
                    durationSeconds = 300,
                ),
            ),
            isTextExpanded = false
        )

        InvitationStoryScreen(
            uiState = mockState,
            initialIndex = 0,
            onPageChanged = {},
            onToggleExpand = {},
            onClose = {}
        )
    }
}
