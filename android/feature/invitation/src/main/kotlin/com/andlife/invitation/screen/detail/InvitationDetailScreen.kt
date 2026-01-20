package com.andlife.invitation.screen.detail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.invitation.R
import com.andlife.invitation.model.detail.InvitationDetailSideEffect
import com.andlife.invitation.model.detail.InvitationDetailUiEvent
import com.andlife.invitation.model.detail.InvitationDetailUiState
import com.andlife.invitation.screen.guestbook.collection.InvitationCollectionRoute
import com.andlife.invitation.screen.guestbook.InvitationGuestBookRoute
import com.andlife.invitation.viewmodel.InvitationDetailViewModel
import com.andlife.ui.component.GenericTabRow
import com.andlife.ui.component.loading.InvitationLoadingError
import com.andlife.ui.component.loading.InvitationLoadingIndicator
import com.andlife.ui.util.collectWithLifecycle
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.andlife.designsystem.R as designR

@Composable
fun InvitationDetailRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InvitationDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val mapErrorMessage = stringResource(R.string.snack_load_error_map)

    viewModel.effectFlow.collectWithLifecycle { effect ->
        when (effect) {
            InvitationDetailSideEffect.NavigateBack -> {
                onNavigateBack()
            }

            InvitationDetailSideEffect.ShowMapErrorSnackbar -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = mapErrorMessage
                    )
                }
            }
        }
    }

    InvitationDetailScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent,
        onNavigateBack = onNavigateBack,
        modifier = modifier,
    )
}

@Composable
private fun InvitationDetailScreen(
    uiState: InvitationDetailUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (InvitationDetailUiEvent) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tabTitles = stringArrayResource(R.array.txt_tap_title).toImmutableList()
    val coroutineScope = rememberCoroutineScope()
    var isMapVisible by remember { mutableStateOf(true) }

    val navigateBackWithMapCleanup: () -> Unit = {
        isMapVisible = false
        coroutineScope.launch {
            delay(50L)
            onEvent(InvitationDetailUiEvent.ClickBack)
        }
    }

    BackHandler(onBack = navigateBackWithMapCleanup)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        topBar = {
            InvitationDetailTopBar(
                title = uiState.invitationContentsUiModel.title,
                hasThanksCard = uiState.hasThanksCard,
                showActions = !uiState.isLoading && !uiState.isError,
                onBack = navigateBackWithMapCleanup,
                onClickThanksCard = { onEvent(InvitationDetailUiEvent.ClickThanksCard) },
                onDelete = { onEvent(InvitationDetailUiEvent.ClickDelete) },
            )
        },
        containerColor = NachoTheme.colorScheme.backgroundPrimary,
    ) { paddingValues ->
        if (uiState.isLoading) {
            InvitationLoadingIndicator(
                modifier = Modifier.padding(paddingValues),
                text = stringResource(R.string.txt_loading_invitation),
            )
            return@Scaffold
        }

        if (uiState.isError) {
            InvitationLoadingError(
                onRetry = { onEvent(InvitationDetailUiEvent.RetryLoad) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            )
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            GenericTabRow(
                tabs = tabTitles,
                content = { index ->
                    when (index) {
                        0 -> {
                            InvitationContentsScreen(
                                uiState = uiState,
                                onClickImage = { idx ->
                                    onEvent(
                                        InvitationDetailUiEvent.ClickImage(
                                            uiState.invitationContentsUiModel.imageList,
                                            idx,
                                        ),
                                    )
                                },
                                onMapError = { onEvent(InvitationDetailUiEvent.MapError) },
                                isMapVisible = isMapVisible,
                                modifier = Modifier.fillMaxSize(),
                            )
                        }

                        1 -> InvitationGuestBookRoute(onNavigateBack = onNavigateBack)
                        2 -> InvitationCollectionRoute()
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
    onClickThanksCard: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    showActions: Boolean = true,
    hasThanksCard: Boolean = false,
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = title,
                style = NachoTheme.typography.headingSmallSemiBold,
                color = NachoTheme.colorScheme.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
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
        actions = {
            if (showActions) {
                if (hasThanksCard) {
                    IconButton(onClick = onClickThanksCard) {
                        Icon(
                            painter = painterResource(designR.drawable.ic_thankscard),
                            contentDescription = stringResource(R.string.desc_top_bar_thanks_card),
                            tint = Color.Unspecified,
                        )
                    }
                }
                InvitationMoreMenu(
                    onDelete = onDelete,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = NachoTheme.colorScheme.backgroundPrimary,
        ),
    )
}


@Composable
private fun InvitationMoreMenu(
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(onClick = { isMenuExpanded = true }) {
            Icon(
                painter = painterResource(designR.drawable.ic_more_vert_24),
                contentDescription = stringResource(R.string.desc_top_bar_more),
                tint = NachoTheme.colorScheme.iconSecondary,
            )
        }

        DropdownMenu(
            expanded = isMenuExpanded,
            onDismissRequest = { isMenuExpanded = false },
            modifier = Modifier.background(NachoTheme.colorScheme.backgroundPrimary),
            shape = RoundedCornerShape(NachoSpacing.medium),
        ) {

            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(R.string.txt_delete),
                        style = NachoTheme.typography.bodyMediumMedium,
                        color = NachoTheme.colorScheme.textPrimary,
                    )
                },
                onClick = {
                    isMenuExpanded = false
                    onDelete()
                },
            )
        }
    }
}

