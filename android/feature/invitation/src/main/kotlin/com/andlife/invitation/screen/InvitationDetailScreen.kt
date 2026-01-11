package com.andlife.invitation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andlife.designsystem.theme.InvitationSpacing
import com.andlife.designsystem.theme.InvitationTheme
import com.andlife.designsystem.R as designR
import com.andlife.invitation.R
import com.andlife.invitation.model.InvitationDetailSideEffect
import com.andlife.invitation.model.InvitationDetailUiEvent
import com.andlife.invitation.model.InvitationDetailUiState
import com.andlife.invitation.viewmodel.InvitationDetailViewModel
import com.andlife.ui.util.collectWithLifecycle

@Composable
fun InvitationDetailRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InvitationDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    viewModel.effectFlow.collectWithLifecycle { effect ->
        when (effect) {
            InvitationDetailSideEffect.NavigateBack -> {
                onNavigateBack()
            }
        }
    }

    InvitationDetailScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        modifier = modifier,
    )
}

@Composable
private fun InvitationDetailScreen(
    uiState: InvitationDetailUiState,
    onEvent: (InvitationDetailUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            InvitationDetailTopBar(
                title = "초대장 id: ${uiState.id}", // TODO: topbar 임시 제목
                onBack = { onEvent(InvitationDetailUiEvent.ClickBack) },
            )
        },
        containerColor = InvitationTheme.colorScheme.backgroundPrimary,
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = InvitationSpacing.medium),
        ) {
            Text(
                text = "InvitationDetailScreen",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InvitationDetailTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = title,
                style = InvitationTheme.typography.headingSmallSemiBold,
                color = InvitationTheme.colorScheme.textPrimary,
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(designR.drawable.ic_arrow_back_24),
                    contentDescription = stringResource(R.string.desc_top_bar_back),
                    tint = InvitationTheme.colorScheme.iconSecondary,
                )
            }
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = InvitationTheme.colorScheme.backgroundPrimary,
            ),
    )
}
