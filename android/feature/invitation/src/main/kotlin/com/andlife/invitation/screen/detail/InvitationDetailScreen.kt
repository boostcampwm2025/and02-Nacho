package com.andlife.invitation.screen.detail

import android.text.Editable
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andlife.designsystem.component.dialog.NachoDialog
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.editor.screen.NachoTextView
import com.andlife.invitation.R
import com.andlife.invitation.model.detail.InvitationDetailSideEffect
import com.andlife.invitation.model.detail.InvitationDetailUiEvent
import com.andlife.invitation.model.detail.InvitationDetailUiState
import com.andlife.invitation.screen.collection.InvitationCollectionRoute
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvitationDetailRoute(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InvitationDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val res = LocalResources.current
    var isThanksCardVisible by remember { mutableStateOf(false) }
    val mapErrorMessage = stringResource(R.string.snack_load_error_map)
    val thanksCard = uiState.invitationContentsUiModel.thanksCard
    val textPrimary = NachoTheme.colorScheme.textPrimary

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

            InvitationDetailSideEffect.ShowLeaveInvitationErrorSnackbar -> {
                coroutineScope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(res.getString(R.string.msg_leave_invitation_error))
                }
            }

            InvitationDetailSideEffect.ThanksCardOnBoarding -> {
                isThanksCardVisible = true
            }
        }
    }

    Box {
        InvitationDetailScreen(
            uiState = uiState,
            snackbarHostState = snackbarHostState,
            scrollBehavior = scrollBehavior,
            onEvent = viewModel::onEvent,
            onNavigateBack = onNavigateBack,
            onNavigateToLogin = onNavigateToLogin,
            onEditableSave = viewModel::saveEditable,
            modifier = modifier,
        )

        if (uiState.isOverlayLoading) {
            InvitationLoadingIndicator()
        }
    }

    if (isThanksCardVisible && thanksCard != null) {
        NachoDialog(
            onDismiss = { isThanksCardVisible = false },
            shape = NachoTheme.shapes.small,
            modifier = Modifier.fillMaxHeight(0.7f),
            containerColor = Color(thanksCard.backgroundColor)
        ) {
            Box(modifier = Modifier.background(Color(thanksCard.backgroundColor))) {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {

                    AndroidView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(NachoSpacing.medium),
                        factory = { context ->
                            NachoTextView(context).apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                )
                                setTextColor(textPrimary.toArgb())
                                onEditableReady = { editable ->
                                    viewModel.saveThanksCardEditableCache(editable)
                                }
                            }
                        },
                        update = { view ->
                            val editableCache = uiState.thanksCardEditableCache
                            if (editableCache != null) {
                                view.bindWithCachedEditable(editableCache)
                            } else {
                                view.bind(uiState.invitationContentsUiModel.thanksCard)
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InvitationDetailScreen(
    uiState: InvitationDetailUiState,
    snackbarHostState: SnackbarHostState,
    scrollBehavior: TopAppBarScrollBehavior,
    onEvent: (InvitationDetailUiEvent) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onEditableSave: (Editable) -> Unit,
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
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        topBar = {
            InvitationDetailTopBar(
                scrollBehavior = scrollBehavior,
                title = uiState.invitationContentsUiModel.title,
                hasThanksCard = uiState.hasThanksCard,
                showActions = !uiState.isLoading && !uiState.isError,
                onBack = navigateBackWithMapCleanup,
                onClickThanksCard = { onEvent(InvitationDetailUiEvent.ClickThanksCard) },
                onLeave = { onEvent(InvitationDetailUiEvent.ClickLeaveInvitation) },
            )
        },
        containerColor = NachoTheme.colorScheme.backgroundPrimary,
    ) { paddingValues ->
        if (uiState.isLoading) {
            InvitationLoadingIndicator(
                text = stringResource(R.string.txt_loading_invitation),
                modifier = Modifier.fillMaxSize(),
            )
            return@Scaffold
        }

        if (uiState.isError) {
            InvitationLoadingError(
                onRetry = { onEvent(InvitationDetailUiEvent.RetryLoad) },
                modifier = Modifier.fillMaxSize(),
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
                                onEditableSave = onEditableSave,
                                modifier = Modifier.fillMaxSize(),
                            )
                        }

                        1 -> InvitationGuestBookRoute(
                            onNavigateBack = onNavigateBack,
                            onNavigateToLogin = onNavigateToLogin
                        )
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
    scrollBehavior: TopAppBarScrollBehavior,
    title: String,
    onBack: () -> Unit,
    onClickThanksCard: () -> Unit,
    onLeave: () -> Unit,
    modifier: Modifier = Modifier,
    showActions: Boolean = true,
    hasThanksCard: Boolean = false,
) {
    val alpha = 1f - scrollBehavior.state.collapsedFraction

    TopAppBar(
        modifier = modifier.graphicsLayer { this.alpha = alpha },
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
                    onLeave = onLeave,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = NachoTheme.colorScheme.backgroundPrimary,
            scrolledContainerColor = NachoTheme.colorScheme.backgroundPrimary,
        ),
        scrollBehavior = scrollBehavior,
    )
}


@Composable
private fun InvitationMoreMenu(
    onLeave: () -> Unit,
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
                        text = stringResource(R.string.txt_leave_invitation),
                        style = NachoTheme.typography.bodyMediumMedium,
                        color = NachoTheme.colorScheme.textPrimary,
                    )
                },
                onClick = {
                    isMenuExpanded = false
                    onLeave()
                },
            )
        }
    }
}

