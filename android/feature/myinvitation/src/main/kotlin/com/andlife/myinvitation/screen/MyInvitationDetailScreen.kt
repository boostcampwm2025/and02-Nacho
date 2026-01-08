package com.andlife.myinvitation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andlife.designsystem.component.InvitationButton
import com.andlife.designsystem.component.InvitationTextField
import com.andlife.designsystem.R as designR
import com.andlife.designsystem.theme.InvitationSpacing
import com.andlife.designsystem.theme.InvitationTheme
import com.andlife.myinvitation.R
import com.andlife.myinvitation.model.MyInvitationDetailSideEffect
import com.andlife.myinvitation.model.MyInvitationDetailUiEvent
import com.andlife.myinvitation.model.MyInvitationDetailUiState
import com.andlife.myinvitation.viewmodel.MyInvitationDetailViewModel
import com.andlife.ui.util.collectWithLifecycle

@Composable
fun MyInvitationDetailRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyInvitationDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    viewModel.effectFlow.collectWithLifecycle { effect ->
        when (effect) {
            MyInvitationDetailSideEffect.NavigateBack -> {
                onNavigateBack()
            }
        }
    }

    MyInvitationDetailScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        modifier = modifier,
    )
}

@Composable
private fun MyInvitationDetailScreen(
    uiState: MyInvitationDetailUiState,
    onEvent: (MyInvitationDetailUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            MyInvitationDetailTopBar(
                title = "초대장 id: ${uiState.id}",
                onBack = { onEvent(MyInvitationDetailUiEvent.ClickBack) },
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
                text = "초대장 딥링크",
                style = InvitationTheme.typography.bodyMediumSemiBold,
                color = InvitationTheme.colorScheme.textPrimary,
                modifier = Modifier.padding(bottom = InvitationSpacing.small)
            )
            InvitationTextField(
                value = uiState.deepLinkUrl,
                onValueChange = { },
                placeholder = "",
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )

            InvitationButton(
                onClick = { onEvent(MyInvitationDetailUiEvent.ClickShare) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("초대장 공유하기")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyInvitationDetailTopBar(
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
                    painter = painterResource( designR.drawable.ic_arrow_back_24),
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
