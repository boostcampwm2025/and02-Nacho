package com.andlife.invitation.screen

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldPredictiveBackHandler
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldValue
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.andlife.ui.component.dialog.NachoInfoDialog
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.domain.model.invitation.SortDirection
import com.andlife.domain.util.RefreshEventHub
import com.andlife.invitation.InvitationDetail
import com.andlife.invitation.InvitationPlaceholder
import com.andlife.invitation.model.InvitationSideEffect
import com.andlife.invitation.model.InvitationUiEvent
import com.andlife.invitation.model.InvitationUiState
import com.andlife.invitation.screen.detail.InvitationDetailRoute
import com.andlife.invitation.screen.detail.InvitationPlaceholderScreen
import com.andlife.invitation.viewmodel.Invitation2PaneUiState
import com.andlife.invitation.viewmodel.Invitation2PaneViewModel
import com.andlife.invitation.viewmodel.InvitationDetailViewModel
import com.andlife.invitation.viewmodel.InvitationGuestBookViewModel
import com.andlife.invitation.viewmodel.InvitationViewModel
import com.andlife.model.invitation.InvitationSummaryUiModel
import com.andlife.ui.R
import com.andlife.ui.component.GenericTabRow
import com.andlife.ui.component.dialog.LoginDialog
import com.andlife.ui.component.invitation.InvitationListHeader
import com.andlife.ui.component.invitation.InvitationTopBar
import com.andlife.ui.component.listitem.InvitationListItem
import com.andlife.ui.component.listitem.MenuItem
import com.andlife.ui.component.loading.InvitationLoadingIndicator
import com.andlife.ui.component.paging.PagingStateContent
import com.andlife.ui.component.report.ReportBottomSheet
import com.andlife.ui.util.DetailPaneViewModelScope
import com.andlife.ui.util.LocalNavigationSuiteState
import com.andlife.ui.util.collectWithLifecycle
import com.andlife.ui.util.isNavigationBar
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

private const val SAMPLE_INVITATION_ID = 1L

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun InvitationsListDetailScreen(
    snackbarHostState: SnackbarHostState,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: Invitation2PaneViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    InvitationListDetailScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onNavigateToLogin = onNavigateToLogin,
        onInvitationClick = viewModel::onInvitationClick
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun InvitationListDetailScreen(
    uiState: Invitation2PaneUiState,
    snackbarHostState: SnackbarHostState,
    onNavigateToLogin: () -> Unit,
    onInvitationClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedInvitationId = uiState.selectedInvitationId

    val suiteState = LocalNavigationSuiteState.current
    val navSuiteType =
        NavigationSuiteScaffoldDefaults.navigationSuiteType(currentWindowAdaptiveInfo())

    val listDetailNavigator = rememberListDetailPaneScaffoldNavigator()
    val coroutineScope = rememberCoroutineScope()

    var invitationRoute by remember {
        val route = selectedInvitationId?.let { InvitationDetail(id = it) } ?: InvitationPlaceholder
        mutableStateOf(route)
    }

    var isConsumeDeepLink by rememberSaveable {
        mutableStateOf(false)
    }

    fun onInvitationClickShowDetailPane(id: Long) {
        Log.d("onInvitationClickShowDetailPane", "onInvitationClickShowDetailPane: $id")
        onInvitationClick(id)
        invitationRoute = InvitationDetail(id)
        coroutineScope.launch {
            if (navSuiteType.isNavigationBar && suiteState.currentValue == NavigationSuiteScaffoldValue.Visible) suiteState.hide()
            listDetailNavigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
        }
    }

    LaunchedEffect(Unit) {
        if (!isConsumeDeepLink && uiState.isFromDeelLink) {
            val id = uiState.selectedInvitationId ?: return@LaunchedEffect
            onInvitationClickShowDetailPane(id)
            isConsumeDeepLink = true
        }
    }

    ThreePaneScaffoldPredictiveBackHandler(
        listDetailNavigator,
        BackNavigationBehavior.PopUntilScaffoldValueChange,
    )

    NavigableListDetailPaneScaffold(
        navigator = listDetailNavigator,
        listPane = {
            AnimatedPane {
                InvitationRoute(
                    snackbarHostState = snackbarHostState,
                    onNavigateToDetail = { onInvitationClickShowDetailPane(it) },
                    onNavigateToLogin = onNavigateToLogin,
                    selectedInvitationId = uiState.selectedInvitationId,
                    shouldHighlightSelected = listDetailNavigator.isDetailPaneVisible(),
                )
            }
        },
        detailPane = {
            AnimatedPane {
                AnimatedContent(invitationRoute) { route ->
                    when (route) {
                        is InvitationDetail -> {
                            DetailPaneViewModelScope {
                                InvitationDetailRoute(
                                    selectedId = route.id,
                                    onNavigateBack = {
                                        coroutineScope.launch {
                                            if (navSuiteType.isNavigationBar && suiteState.currentValue == NavigationSuiteScaffoldValue.Hidden) suiteState.show()
                                            listDetailNavigator.navigateBack()
                                        }
                                    },
                                    onNavigateToLogin = onNavigateToLogin,
                                    showBackButton = !listDetailNavigator.isListPaneVisible(),
                                    viewModel = hiltViewModel<InvitationDetailViewModel, InvitationDetailViewModel.Factory>(
                                        key = "detail ${route.id}"
                                    ) { factory ->
                                        factory.create(route.id, uiState.isFromDeelLink)
                                    },
                                    guestBookViewModel = hiltViewModel<InvitationGuestBookViewModel, InvitationGuestBookViewModel.Factory>(
                                        key = "guestbook ${route.id}"
                                    ) { factory ->
                                        factory.create(route.id)
                                    }
                                )
                            }
                        }
                        is InvitationPlaceholder -> {
                            InvitationPlaceholderScreen()
                        }
                    }
                }
            }
        },
        modifier = modifier.background(NachoTheme.colorScheme.backgroundPrimary),
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private fun <T> ThreePaneScaffoldNavigator<T>.isListPaneVisible(): Boolean =
    scaffoldValue[ListDetailPaneScaffoldRole.List] == PaneAdaptedValue.Expanded

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private fun <T> ThreePaneScaffoldNavigator<T>.isDetailPaneVisible(): Boolean =
    scaffoldValue[ListDetailPaneScaffoldRole.Detail] == PaneAdaptedValue.Expanded

@Composable
fun InvitationRoute(
    snackbarHostState: SnackbarHostState,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    selectedInvitationId: Long? = null,
    shouldHighlightSelected: Boolean = false,
    viewModel: InvitationViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val needsRefresh by RefreshEventHub.invitationRefresh.collectAsStateWithLifecycle()

    val upcomingItems = viewModel.upcomingInvitationPagingFlow.collectAsLazyPagingItems()
    val pastItems = viewModel.pastInvitationPagingFlow.collectAsLazyPagingItems()

    var invitationIdToLeave by remember { mutableStateOf<Long?>(null) }

    val scope = rememberCoroutineScope()

    val refreshFailureMessage = stringResource(R.string.msg_refresh_failure)
    val leaveSuccessMessage = stringResource(R.string.msg_leave_success)
    val leaveFailureMessage = stringResource(R.string.msg_leave_failure)
    val reportSuccessMessage = stringResource(R.string.msg_report_success)
    val reportFailureMessage = stringResource(R.string.msg_report_failure)

    viewModel.effectFlow.collectWithLifecycle { effect ->
        when (effect) {
            is InvitationSideEffect.NavigateToDetail -> onNavigateToDetail(effect.id)
            is InvitationSideEffect.RefreshFailure -> {
                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(refreshFailureMessage)
                }
            }
            is InvitationSideEffect.LeaveSuccess -> {
                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(leaveSuccessMessage)
                }
                upcomingItems.refresh()
                pastItems.refresh()
            }
            is InvitationSideEffect.LeaveFailure -> {
                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(leaveFailureMessage)
                }
            }
            is InvitationSideEffect.NeedRefresh -> {
                upcomingItems.refresh()
                pastItems.refresh()
            }
            InvitationSideEffect.ReportSuccess -> {
                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(reportSuccessMessage)
                }
            }
            is InvitationSideEffect.ReportFailure -> {
                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(effect.message ?: reportFailureMessage)
                }
            }
        }
    }

    LaunchedEffect(needsRefresh) {
        Log.d("RefreshEventHub", "invitation needsRefresh: $needsRefresh")
        if (needsRefresh) {
            viewModel.handleRefresh()
            RefreshEventHub.consumeInvitation()
        }
    }

    LaunchedEffect(upcomingItems.loadState.refresh, pastItems.loadState.refresh) {
        val isNotLoading = upcomingItems.loadState.refresh !is LoadState.Loading &&
            pastItems.loadState.refresh !is LoadState.Loading

        if (isNotLoading && uiState.isRefreshing) {
            viewModel.onRefreshFinished(
                hasError = upcomingItems.loadState.refresh is LoadState.Error ||
                    pastItems.loadState.refresh is LoadState.Error
            )
        }
    }

    invitationIdToLeave?.let { id ->
        NachoInfoDialog(
            title = stringResource(R.string.txt_leave_invitation_title),
            message = stringResource(R.string.txt_leave_invitation_message),
            confirmText = stringResource(R.string.txt_confirm),
            dismissText = stringResource(R.string.txt_cancel),
            onConfirm = {
                viewModel.leaveInvitation(id)
                invitationIdToLeave = null
            },
            onDismiss = { invitationIdToLeave = null },
        )
    }

    if (uiState.reportTargetId != null) {
        ReportBottomSheet(
            onSubmit = { reason, description ->
                viewModel.onEvent(InvitationUiEvent.SubmitReport(reason, description))
            },
            onDismiss = {
                viewModel.onEvent(InvitationUiEvent.DismissReport)
            }
        )
    }

    if (uiState.showLoginDialog) {
        LoginDialog(
            onDismiss = {
                viewModel.onEvent(InvitationUiEvent.DismissLoginDialog)
            },
            onConfirm = {
                viewModel.onEvent(InvitationUiEvent.DismissLoginDialog)
                onNavigateToLogin()
            }
        )
    }

    InvitationScreen(
        uiState = uiState,
        upcomingItems = upcomingItems,
        pastItems = pastItems,
        modifier = modifier,
        onEvent = viewModel::onEvent,
        onLeaveClick = { invitationIdToLeave = it },
        selectedInvitationId = selectedInvitationId,
        shouldHighlightSelected = shouldHighlightSelected
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InvitationScreen(
    uiState: InvitationUiState,
    upcomingItems: LazyPagingItems<InvitationSummaryUiModel>,
    pastItems: LazyPagingItems<InvitationSummaryUiModel>,
    onEvent: (InvitationUiEvent) -> Unit,
    onLeaveClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    selectedInvitationId: Long? = null,
    shouldHighlightSelected: Boolean = false,
) {
    val tabs = stringArrayResource(R.array.arr_invitation_tabs).toImmutableList()
    val upcomingSortOptions = stringArrayResource(R.array.arr_invitation_sort_options).toImmutableList()
    val pastSortOptions = stringArrayResource(R.array.arr_past_invitation_sort_options).toImmutableList()
    var upcomingSortIndex by remember { mutableIntStateOf(0) }
    var pastSortIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            InvitationTopBar(
                stringResource(R.string.txt_invitation)
            )
        },
        contentWindowInsets = WindowInsets(),
        containerColor = NachoTheme.colorScheme.backgroundPrimary,
    ) { paddingValues ->
        GenericTabRow(
            tabs = tabs,
            modifier = Modifier.padding(paddingValues),
            content = { pageIndex ->
                LaunchedEffect(pageIndex) {
                    onEvent(InvitationUiEvent.SelectTab(pageIndex))
                }

                val isUpcoming = pageIndex == 0
                val currentItems = if (isUpcoming) upcomingItems else pastItems
                val currentSortOptions = if (isUpcoming) upcomingSortOptions else pastSortOptions
                val currentSortIndex = if (isUpcoming) upcomingSortIndex else pastSortIndex

                PullToRefreshBox(
                    isRefreshing = uiState.isRefreshing,
                    onRefresh = {
                        currentItems.refresh()
                        onEvent(InvitationUiEvent.Refresh)
                    },
                    modifier = Modifier.fillMaxSize()
                ) {
                    PagingStateContent(
                        loadState = currentItems.loadState.source.refresh,
                        itemCount = currentItems.itemCount,
                        mediatorLoadState = currentItems.loadState.mediator?.refresh,
                        emptyComment = stringResource(R.string.label_invitation_empty),
                        onRetry = { currentItems.retry() }
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(NachoSpacing.large),
                            verticalArrangement = Arrangement.spacedBy(NachoSpacing.medium)
                        ) {
                            if (currentItems.itemCount > 0) {
                                item {
                                    InvitationListHeader(
                                        totalCount = if (isUpcoming) uiState.upcomingTotalCount else uiState.pastTotalCount,
                                        currentSort = currentSortOptions[currentSortIndex],
                                        sortOptions = currentSortOptions,
                                        onSortSelected = { index ->
                                            if (isUpcoming) {
                                                upcomingSortIndex = index
                                            } else {
                                                pastSortIndex = index
                                            }

                                            val newDirection = if (isUpcoming) {
                                                if (index == 0) SortDirection.ASC else SortDirection.DESC
                                            } else {
                                                if (index == 0) SortDirection.DESC else SortDirection.ASC
                                            }

                                            onEvent(InvitationUiEvent.ChangeSort(
                                                isUpcoming = isUpcoming,
                                                newSort = newDirection
                                            ))
                                        }
                                    )
                                }
                            }

                            items(
                                count = currentItems.itemCount,
                                key = currentItems.itemKey { it.id }
                            ) { index ->
                                currentItems[index]?.let { invitation ->
                                    val isSelected = shouldHighlightSelected && invitation.id == selectedInvitationId
                                    val dDayLabel = when (val count = invitation.dDayCount) {
                                        null -> null
                                        0 -> stringResource(R.string.format_invitation_d_day_today)
                                        else -> stringResource(R.string.format_invitation_d_day, count)
                                    }

                                    val menuItems = if (invitation.id != SAMPLE_INVITATION_ID) {
                                        persistentListOf(
                                            MenuItem(
                                                title = stringResource(R.string.txt_leave_invitation_title),
                                                onClick = { onLeaveClick(invitation.id) }
                                            ),
                                            MenuItem(
                                                title = stringResource(R.string.txt_report),
                                                onClick = { onEvent(InvitationUiEvent.ShowReport(invitation.id)) }
                                            )
                                        )
                                    } else {
                                        persistentListOf(
                                            MenuItem(
                                                title = stringResource(R.string.txt_leave_invitation_title),
                                                onClick = { onLeaveClick(invitation.id) }
                                            )
                                        )
                                    }

                                    InvitationListItem(
                                        imageUrl = invitation.thumbnailUrls.firstOrNull() ?: "",
                                        title = invitation.title,
                                        startTime = invitation.invitationDateTime,
                                        hostName = invitation.displayHostName,
                                        address = invitation.address,
                                        dDayText = dDayLabel,
                                        onClick = { onEvent(InvitationUiEvent.ClickInvitation(invitation.id)) },
                                        menuItems = menuItems,
                                        isSelected = isSelected
                                    )
                                }
                            }

                            if (currentItems.loadState.append is LoadState.Loading) {
                                item {
                                    InvitationLoadingIndicator(modifier = Modifier.padding(NachoSpacing.medium))
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}

/**
 * 딥링크인가?
 * 2paneViewModel에서 event를 가진다. (채널로 가진다)
 * 딥링크가 true라면 이벤트를 한번 소비시킨다.
 * 그 후 다른 초대장을 클릭하면? false로 이동하긴 해야 한다. true false 값이 바뀌어야 함, 딥링크로 들어온 id들을 배열로 관리하면?(상태관리)
 * 값은
 */
