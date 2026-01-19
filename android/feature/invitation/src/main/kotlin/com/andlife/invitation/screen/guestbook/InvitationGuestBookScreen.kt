package com.andlife.invitation.screen.guestbook

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.invitation.model.guestbook.InvitationGuestBookSideEffect
import com.andlife.invitation.model.guestbook.InvitationGuestBookUiEvent
import com.andlife.invitation.model.guestbook.InvitationGuestBookUiState
import com.andlife.invitation.viewmodel.InvitationGuestBookViewModel
import com.andlife.ui.component.invitation.InvitationGuestBookForm
import com.andlife.ui.util.collectWithLifecycle

@Composable
fun InvitationGuestBookRoute(
    modifier: Modifier = Modifier,
    viewModel: InvitationGuestBookViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    viewModel.effectFlow.collectWithLifecycle { effect ->
        when (effect) {
            is InvitationGuestBookSideEffect.ShowSnackbar -> {
                snackbarHostState.showSnackbar(
                    message = effect.message,
                    duration = SnackbarDuration.Short,
                )
            }
        }
    }

    InvitationGuestBookScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    )
}

@Composable
private fun InvitationGuestBookScreen(
    uiState: InvitationGuestBookUiState,
    onEvent: (InvitationGuestBookUiEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(NachoSpacing.large),
        verticalArrangement = Arrangement.spacedBy(NachoSpacing.xLarge),
    ) {
        TitleSection()

        GuestBookFormSection(
            uiState = uiState,
            onEvent = onEvent,
        )

        // Snackbar
        SnackbarHost(hostState = snackbarHostState)
    }
}

@Composable
private fun TitleSection() {
    Text(text = "InvitationGuestBookScreen")
}

@Composable
private fun GuestBookFormSection(
    uiState: InvitationGuestBookUiState,
    onEvent: (InvitationGuestBookUiEvent) -> Unit,
) {
    InvitationGuestBookForm(
        selectedMedias = uiState.selectedMedias,
        textContent = uiState.textContent,
        isUploading = uiState.isUploading,
        onMediasSelected = { medias ->
            onEvent(InvitationGuestBookUiEvent.UpdateSelectedMedias(medias))
        },
        onMediaRemove = { media ->
            onEvent(InvitationGuestBookUiEvent.RemoveMedia(media))
        },
        onTextContentChange = { text ->
            onEvent(InvitationGuestBookUiEvent.UpdateTextContent(text))
        },
        onUploadClick = {
            onEvent(InvitationGuestBookUiEvent.UploadMedias)
        },
    )
}

@Composable
@Preview
private fun InvitationGuestBookScreenPreview() {
    NachoTheme {
        InvitationGuestBookScreen(
            uiState = InvitationGuestBookUiState(),
            onEvent = {},
            snackbarHostState = SnackbarHostState(),
        )
    }
}
