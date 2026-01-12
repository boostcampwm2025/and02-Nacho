package com.andlife.invitation.screen.guestbook

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.InvitationIconSize
import com.andlife.designsystem.theme.InvitationSpacing
import com.andlife.designsystem.theme.InvitationTheme
import com.andlife.domain.model.guestbook.MediaType
import com.andlife.invitation.component.StoryContent
import com.andlife.invitation.model.guestbook.collection.InvitationCollectionUiModel
import com.andlife.invitation.model.guestbook.collection.InvitationCollectionUiState
import com.andlife.invitation.util.toDateTimeFormat
import com.andlife.invitation.util.toUiType
import com.andlife.invitation.viewmodel.InvitationCollectionViewModel
import com.andlife.ui.R
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
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

    InvitationStoryScreen(
        uiState = uiState,
        initialIndex = initialIndex,
        onPageChanged = onPageChanged,
        onToggleExpand = onToggleExpand,
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

@Composable
fun StoryTopHeader(
    name: String,
    date: LocalDateTime,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    profileUrl: String? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onClose) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back_24),
                contentDescription = "Back",
                tint = InvitationTheme.colorScheme.iconTertiary,
            )
        }

        Box(
            modifier =
                Modifier
                    .size(InvitationIconSize.large)
                    .clip(CircleShape)
                    .background(InvitationTheme.colorScheme.backgroundSecondary),
            contentAlignment = Alignment.Center,
        ) {
            if (!profileUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = profileUrl,
                    contentDescription = stringResource(R.string.desc_user_profile),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.ic_person_24),
                    contentDescription = stringResource(R.string.desc_btn_back),
                    tint = InvitationTheme.colorScheme.iconSecondary,
                    modifier = Modifier.size(InvitationIconSize.medium),
                )
            }
        }

        Spacer(modifier = Modifier.width(InvitationSpacing.medium))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                color = InvitationTheme.colorScheme.textOnPrimary,
                style = InvitationTheme.typography.bodyMediumMedium,
            )
            Text(
                text = date.toDateTimeFormat(),
                color = InvitationTheme.colorScheme.textTertiary,
                style = InvitationTheme.typography.bodySmallRegular,
            )
        }

        IconButton(onClick = { /* Todo: 다운로드 로직 */ }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_download_24),
                contentDescription = stringResource(R.string.desc_btn_download),
                tint = InvitationTheme.colorScheme.iconTertiary,
            )
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

@PreviewTheme
@Composable
private fun StoryTopHeaderPreview() {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    InvitationTheme {
        StoryTopHeader(
            name = "사용자 이름",
            date = now,
            onClose = {},
        )
    }
}
