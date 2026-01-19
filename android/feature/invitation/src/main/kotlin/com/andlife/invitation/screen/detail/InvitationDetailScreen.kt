package com.andlife.invitation.screen.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.invitation.R
import com.andlife.invitation.model.detail.InvitationDetailSideEffect
import com.andlife.invitation.model.detail.InvitationDetailUiEvent
import com.andlife.invitation.model.detail.InvitationDetailUiState
import com.andlife.invitation.screen.guestbook.InvitationGuestBookRoute
import com.andlife.invitation.screen.guestbook.InvitationCollectionRoute
import com.andlife.invitation.viewmodel.InvitationDetailViewModel
import com.andlife.ui.component.GenericTabRow
import com.andlife.ui.util.collectWithLifecycle
import kotlinx.collections.immutable.toImmutableList
import com.andlife.designsystem.R as designR

private const val TAG = "InvitationDetailScreen"

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
    val tabTitles = stringArrayResource(R.array.txt_tap_title).toImmutableList()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            InvitationDetailTopBar(
                title = "초대장 id: ${uiState.id}", // TODO: topbar 임시 제목
                onBack = { onEvent(InvitationDetailUiEvent.ClickBack) },
            )
        },
        containerColor = NachoTheme.colorScheme.backgroundPrimary,
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
        ) {
            GenericTabRow(
                tabs = tabTitles,
                content = { index ->
                    Column(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(horizontal = NachoSpacing.medium)
                    ) {
                        when (index) {
                            0 -> Text("초대장 콘텐츠")
                            1 -> InvitationGuestBookRoute()
                            2 -> InvitationCollectionRoute()
                        }
                    }
                },
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
                style = NachoTheme.typography.headingSmallSemiBold,
                color = NachoTheme.colorScheme.textPrimary,
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(designR.drawable.ic_arrow_back_24),
                    contentDescription = stringResource(R.string.desc_top_bar_back),
                    tint = NachoTheme.colorScheme.iconSecondary,
                )
            }
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = NachoTheme.colorScheme.backgroundPrimary,
            ),
    )
}
