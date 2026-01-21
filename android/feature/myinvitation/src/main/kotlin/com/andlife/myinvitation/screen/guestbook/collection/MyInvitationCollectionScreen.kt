package com.andlife.myinvitation.screen.guestbook.collection

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
import com.andlife.model.invitation.collection.CollectionUiModel
import com.andlife.model.util.toUiType
import com.andlife.myinvitation.model.guestbook.collection.MyInvitationCollectionUiEvent
import com.andlife.myinvitation.model.guestbook.collection.MyInvitationCollectionUiState
import com.andlife.myinvitation.viewmodel.MyInvitationCollectionViewModel
import com.andlife.ui.component.invitation.collection.NachoMediaGridView
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun MyInvitationCollectionRoute(
    modifier: Modifier = Modifier,
    viewModel: MyInvitationCollectionViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MyInvitationCollectionScreen(
        uiState = uiState,
        onOpenStory = { index ->
            viewModel.onEvent(MyInvitationCollectionUiEvent.OpenStory(index))
        },
        onCloseStory = {
            viewModel.onEvent(MyInvitationCollectionUiEvent.CloseStory)
        },
        onPageChanged = { index ->
            viewModel.onEvent(MyInvitationCollectionUiEvent.PageChanged(index))
        },
        onToggleExpand = {
            viewModel.onEvent(MyInvitationCollectionUiEvent.ToggleExpand)
        },
        modifier = modifier,
    )
}

@Composable
fun MyInvitationCollectionScreen(
    uiState: MyInvitationCollectionUiState,
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
            MyInvitationStoryRoute(
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
private fun MyInvitationCollectionScreenPreview() {
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

    NachoTheme {
        MyInvitationCollectionScreen(
            uiState = mockState,
            onOpenStory = {},
            onCloseStory = {},
            onPageChanged = {},
            onToggleExpand = {},
        )
    }
}
