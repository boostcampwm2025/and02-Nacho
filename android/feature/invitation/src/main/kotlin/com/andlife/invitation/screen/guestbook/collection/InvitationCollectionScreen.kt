package com.andlife.invitation.screen.guestbook.collection

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.domain.model.guestbook.MediaType
import com.andlife.ui.component.collection.NachoMediaGridView
import com.andlife.invitation.model.guestbook.collection.InvitationCollectionUiEvent
import com.andlife.invitation.model.guestbook.collection.InvitationCollectionUiState
import com.andlife.model.util.toUiType
import com.andlife.invitation.viewmodel.InvitationCollectionViewModel
import com.andlife.model.invitation.collection.CollectionUiModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun InvitationCollectionRoute(
    modifier: Modifier = Modifier,
    viewModel: InvitationCollectionViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    InvitationCollectionScreen(
        uiState = uiState,
        onOpenStory = { index ->
            viewModel.onEvent(InvitationCollectionUiEvent.OpenStory(index))
        },
        onCloseStory = {
            viewModel.onEvent(InvitationCollectionUiEvent.CloseStory)
        },
        onPageChanged = { index ->
            viewModel.onEvent(InvitationCollectionUiEvent.PageChanged(index))
        },
        onToggleExpand = {
            viewModel.onEvent(InvitationCollectionUiEvent.ToggleExpand)
        },
        modifier = modifier,
    )
}

@Composable
fun InvitationCollectionScreen(
    uiState: InvitationCollectionUiState,
    onOpenStory: (Int) -> Unit,
    onCloseStory: () -> Unit,
    onPageChanged: (Int) -> Unit,
    onToggleExpand: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NachoMediaGridView(
        items = uiState.mediaItems,
        onItemClick = onOpenStory,
        modifier = modifier,
    )

    if (uiState.isDetailMode) {
        Dialog(
            onDismissRequest = onCloseStory,
            properties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            InvitationStoryRoute(
                initialIndex = uiState.selectedIndex,
                onPageChanged = onPageChanged,
                onToggleExpand = onToggleExpand,
                onClose = onCloseStory,
            )
        }
    }
}

@PreviewTheme
@Composable
private fun NachoCollectionPreview() {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val mockState =
        InvitationCollectionUiState(
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

    NachoTheme {
        InvitationCollectionScreen(
            uiState = mockState,
            onOpenStory = {},
            onCloseStory = {},
            onPageChanged = {},
            onToggleExpand = {},
        )
    }
}
