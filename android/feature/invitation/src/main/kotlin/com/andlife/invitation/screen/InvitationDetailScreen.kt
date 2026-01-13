package com.andlife.invitation.screen

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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andlife.designsystem.theme.InvitationSpacing
import com.andlife.designsystem.theme.InvitationTheme
import com.andlife.invitation.model.detail.InvitationDetailSideEffect
import com.andlife.invitation.model.detail.InvitationDetailUiEvent
import com.andlife.invitation.model.detail.InvitationDetailUiState
import com.andlife.invitation.viewmodel.InvitationDetailViewModel
import com.andlife.ui.component.invitation.InvitationGuestBookForm
import com.andlife.ui.util.collectWithLifecycle

@Composable
fun InvitationDetailRoute(
    modifier: Modifier = Modifier,
    viewModel: InvitationDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    viewModel.effectFlow.collectWithLifecycle { effect ->
        when (effect) {
            is InvitationDetailSideEffect.ShowSnackbar -> {
                snackbarHostState.showSnackbar(
                    message = effect.message,
                    duration = SnackbarDuration.Short,
                )
            }
        }
    }

    InvitationDetailScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    )
}

@Composable
private fun InvitationDetailScreen(
    uiState: InvitationDetailUiState,
    onEvent: (InvitationDetailUiEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(InvitationSpacing.large),
        verticalArrangement = Arrangement.spacedBy(InvitationSpacing.xLarge),
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
    Text(text = "InvitationDetailScreen")
}

@Composable
private fun GuestBookFormSection(
    uiState: InvitationDetailUiState,
    onEvent: (InvitationDetailUiEvent) -> Unit,
) {
    InvitationGuestBookForm(
        selectedMedias = uiState.selectedMedias,
        isUploading = uiState.isUploading,
        onMediasSelected = { medias ->
            onEvent(InvitationDetailUiEvent.UpdateSelectedMedias(medias))
        },
        onMediaRemove = { media ->
            onEvent(InvitationDetailUiEvent.RemoveMedia(media))
        },
        onUploadClick = {
            onEvent(InvitationDetailUiEvent.UploadMedias)
        },
    )
}

@Composable
@Preview
private fun InvitationDetailScreenPreview() {
    InvitationTheme {
        InvitationDetailScreen(
            uiState = InvitationDetailUiState(),
            onEvent = {},
            snackbarHostState = SnackbarHostState(),
        )
    }
}
