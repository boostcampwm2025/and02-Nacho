package com.andlife.myinvitation.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.andlife.designsystem.component.NachoButton
import com.andlife.designsystem.component.dialog.NachoDialog
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.domain.model.auth.AuthState
import com.andlife.domain.model.invitation.SortDirection
import com.andlife.model.invitation.InvitationSummaryUiModel
import com.andlife.myinvitation.MyInvitationDetail
import com.andlife.myinvitation.MyInvitationPlaceholder
import com.andlife.myinvitation.model.MyInvitationSideEffect
import com.andlife.myinvitation.model.MyInvitationUiEvent
import com.andlife.myinvitation.model.MyInvitationUiState
import com.andlife.myinvitation.screen.detail.MyInvitationDetailRoute
import com.andlife.myinvitation.screen.detail.MyInvitationPlaceholderScreen
import com.andlife.myinvitation.viewmodel.MyInvitation2PaneViewModel
import com.andlife.myinvitation.viewmodel.MyInvitationDetailViewModel
import com.andlife.myinvitation.viewmodel.MyInvitationGuestBookViewModel
import com.andlife.myinvitation.viewmodel.MyInvitationViewModel
import com.andlife.ui.R
import com.andlife.ui.component.GenericTabRow
import com.andlife.ui.component.invitation.InvitationListHeader
import com.andlife.ui.component.invitation.InvitationTopBar
import com.andlife.ui.component.listitem.InvitationListItem
import com.andlife.ui.component.listitem.MenuItem
import com.andlife.ui.component.loading.InvitationLoadingIndicator
import com.andlife.ui.component.paging.PagingStateContent
import com.andlife.ui.util.DetailPaneViewModelScope
import com.andlife.ui.util.LocalNavigationSuiteState
import com.andlife.ui.util.collectWithLifecycle
import com.andlife.ui.util.isNavigationBar
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import com.andlife.designsystem.R as designR

@Composable
fun MyInvitationScreen(
    snackbarHostState: SnackbarHostState,
    onNavigateToLogin: () -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToEditInvitation: (Long) -> Unit,
    onNavigateToEditCard: (Long) -> Unit,
    onNavigateToCreateCard: (Long) -> Unit,
    onNavigateToCreateThanksCard: (Long) -> Unit,
    onNavigateToUpdateThanksCard: (Long) -> Unit,
    viewModel: MyInvitation2PaneViewModel = hiltViewModel()
) {
    val uiState by viewModel.initialInvitationId.collectAsStateWithLifecycle()

    MyInvitationScreen(
        initialInvitationId = uiState,
        snackbarHostState = snackbarHostState,
        onNavigateToLogin = onNavigateToLogin,
        onNavigateToCreate = onNavigateToCreate,
        onNavigateToEditInvitation = onNavigateToEditInvitation,
        onNavigateToEditCard = onNavigateToEditCard,
        onNavigateToCreateCard = onNavigateToCreateCard,
        onNavigateToCreateThanksCard = onNavigateToCreateThanksCard,
        onNavigateToUpdateThanksCard = onNavigateToUpdateThanksCard,
        onNavigateToDetail = viewModel::setInitialInvitationId,
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun MyInvitationScreen(
    initialInvitationId: Long?,
    snackbarHostState: SnackbarHostState,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToEditInvitation: (Long) -> Unit,
    onNavigateToEditCard: (Long) -> Unit,
    onNavigateToCreateCard: (Long) -> Unit,
    onNavigateToCreateThanksCard: (Long) -> Unit,
    onNavigateToUpdateThanksCard: (Long) -> Unit,
    modifier: Modifier = Modifier
) {

    val suiteState = LocalNavigationSuiteState.current

    val navSuiteType =
        NavigationSuiteScaffoldDefaults.navigationSuiteType(currentWindowAdaptiveInfo())

    val listDetailNavigator = rememberListDetailPaneScaffoldNavigator()
    val coroutineScope = rememberCoroutineScope()

    var myInvitationRoute by remember {
        val route = initialInvitationId?.let { MyInvitationDetail(id = it) } ?: MyInvitationPlaceholder
        mutableStateOf(route)
    }

    fun onInvitationClickShowDetailPane(id: Long) {
        onNavigateToDetail(id)
        myInvitationRoute = MyInvitationDetail(id)
        coroutineScope.launch {
            if (navSuiteType.isNavigationBar && suiteState.currentValue == NavigationSuiteScaffoldValue.Visible) suiteState.hide()
            listDetailNavigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
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
                MyInvitationRoute(
                    snackbarHostState = snackbarHostState,
                    onNavigateToDetail = { onInvitationClickShowDetailPane(it) },
                    onNavigateToLogin = onNavigateToLogin,
                    onNavigateToCreate = onNavigateToCreate,
                    selectedInvitationId = initialInvitationId,
                    shouldHighlightSelected = listDetailNavigator.isDetailPaneVisible(),
                )
            }
        },
        detailPane = {
            AnimatedPane {
                AnimatedContent(myInvitationRoute) { route ->
                    when (route) {
                        is MyInvitationDetail -> {
                            DetailPaneViewModelScope {
                                MyInvitationDetailRoute(
                                    selectedId = route.id,
                                    onNavigateBack = {
                                        coroutineScope.launch {
                                            if (navSuiteType.isNavigationBar && suiteState.currentValue == NavigationSuiteScaffoldValue.Hidden) suiteState.show()
                                            listDetailNavigator.navigateBack()
                                        }
                                    },
                                    onNavigateToLogin = onNavigateToLogin,
                                    onNavigateToEditCard = onNavigateToEditCard,
                                    onNavigateToEditInvitation = onNavigateToEditInvitation,
                                    onNavigateToCreateCard = onNavigateToCreateCard,
                                    onNavigateToCreateThanksCard = onNavigateToCreateThanksCard,
                                    onNavigateToUpdateThanksCard = onNavigateToUpdateThanksCard,
                                    showBackButton = !listDetailNavigator.isListPaneVisible(),
                                    viewModel = hiltViewModel<MyInvitationDetailViewModel, MyInvitationDetailViewModel.Factory>(
                                        key = "myDetail ${route.id}"
                                    ) { factory ->
                                        factory.create(route.id)
                                    },
                                    guestBookViewModel = hiltViewModel<MyInvitationGuestBookViewModel, MyInvitationGuestBookViewModel.Factory>(
                                        key = "myDetail guestBook ${route.id}"
                                    ) { factory ->
                                        factory.create(route.id)
                                    }
                                )
                            }
                        }

                        MyInvitationPlaceholder -> {
                            MyInvitationPlaceholderScreen()
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
fun MyInvitationRoute(
    snackbarHostState: SnackbarHostState,
    onNavigateToCreate: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToLogin: () -> Unit,
    selectedInvitationId: Long? = null,
    shouldHighlightSelected: Boolean = false,
    modifier: Modifier = Modifier,
    viewModel: MyInvitationViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val upcomingItems = viewModel.upcomingMyInvitationPagingFlow.collectAsLazyPagingItems()
    val pastItems = viewModel.pastMyInvitationPagingFlow.collectAsLazyPagingItems()
    val scope = rememberCoroutineScope()
    var invitationIdToDelete by remember { mutableStateOf<Long?>(null) }

    val refreshFailureMessage = stringResource(R.string.msg_refresh_failure)
    val deleteSuccessMessage = stringResource(R.string.msg_delete_success)
    val deleteFailureMessage = stringResource(R.string.msg_delete_failure)

    viewModel.effectFlow.collectWithLifecycle { effect ->
        when (effect) {
            is MyInvitationSideEffect.NavigateToDetail -> onNavigateToDetail(effect.id)
            is MyInvitationSideEffect.RefreshFailure -> {
                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(refreshFailureMessage)
                }
            }

            is MyInvitationSideEffect.NavigateToCreate -> onNavigateToCreate()
            is MyInvitationSideEffect.DeleteSuccess -> {
                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(deleteSuccessMessage)
                }
                upcomingItems.refresh()
                pastItems.refresh()
            }

            is MyInvitationSideEffect.DeleteFailure -> {
                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(deleteFailureMessage)
                }
            }

            is MyInvitationSideEffect.NeedRefresh -> {
                upcomingItems.refresh()
                pastItems.refresh()
            }

            MyInvitationSideEffect.NavigateToLogin -> onNavigateToLogin()
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

    invitationIdToDelete?.let { id ->
        NachoDialog(
            onDismiss = { invitationIdToDelete = null }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(NachoSpacing.xLarge)
            ) {
                Text(
                    text = stringResource(R.string.txt_delete_invitation_title),
                    style = NachoTheme.typography.headingSmallSemiBold,
                    color = NachoTheme.colorScheme.textPrimary
                )

                Text(
                    text = stringResource(R.string.txt_delete_invitation_message),
                    modifier = Modifier.padding(top = NachoSpacing.medium, bottom = NachoSpacing.xLarge),
                    style = NachoTheme.typography.bodyMediumMedium,
                    color = NachoTheme.colorScheme.textSecondary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { invitationIdToDelete = null }
                    ) {
                        Text(
                            text = stringResource(R.string.txt_cancel),
                            color = NachoTheme.colorScheme.textSecondary
                        )
                    }

                    Spacer(modifier = Modifier.padding(horizontal = NachoSpacing.small))

                    TextButton(
                        onClick = {
                            viewModel.deleteInvitation(id)
                            invitationIdToDelete = null
                        },
                        shape = NachoTheme.shapes.small
                    ) {
                        Text(
                            text = stringResource(R.string.txt_confirm),
                            color = NachoTheme.colorScheme.brandPrimary
                        )
                    }
                }
            }
        }
    }

    MyInvitationScreen(
        uiState = uiState,
        authState = authState,
        upcomingItems = upcomingItems,
        pastItems = pastItems,
        modifier = modifier,
        onEvent = viewModel::onEvent,
        onDeleteClick = { invitationIdToDelete = it },
        selectedInvitationId = selectedInvitationId,
        shouldHighlightSelected = shouldHighlightSelected,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyInvitationScreen(
    uiState: MyInvitationUiState,
    authState: AuthState,
    upcomingItems: LazyPagingItems<InvitationSummaryUiModel>,
    pastItems: LazyPagingItems<InvitationSummaryUiModel>,
    onEvent: (MyInvitationUiEvent) -> Unit,
    onDeleteClick: (Long) -> Unit,
    selectedInvitationId: Long? = null,
    shouldHighlightSelected: Boolean = false,
    modifier: Modifier = Modifier,
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
                stringResource(R.string.txt_my_invitation)
            )
        },
        floatingActionButton = {
            if (authState is AuthState.Authenticated) {
                FloatingActionButton(
                    onClick = { onEvent(MyInvitationUiEvent.ClickCreate) },
                    containerColor = NachoTheme.colorScheme.brandPrimary,
                    contentColor = NachoTheme.colorScheme.brandOnPrimary,
                    modifier = Modifier.padding(NachoSpacing.medium)
                ) {
                    Icon(
                        painter = painterResource(id = designR.drawable.ic_add_24),
                        contentDescription = stringResource(R.string.desc_invitation_create)
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets(),
        containerColor = NachoTheme.colorScheme.backgroundPrimary,
    ) { paddingValues ->
        when (authState) {
            is AuthState.Authenticated -> {
                GenericTabRow(
                    tabs = tabs,
                    modifier = Modifier.padding(paddingValues),
                    content = { pageIndex ->
                        LaunchedEffect(pageIndex) {
                            onEvent(MyInvitationUiEvent.SelectTab(pageIndex))
                        }

                        val isUpcoming = pageIndex == 0
                        val currentItems = if (isUpcoming) upcomingItems else pastItems
                        val currentSortOptions = if (isUpcoming) upcomingSortOptions else pastSortOptions
                        val currentSortIndex = if (isUpcoming) upcomingSortIndex else pastSortIndex

                        PullToRefreshBox(
                            isRefreshing = uiState.isRefreshing,
                            onRefresh = {
                                currentItems.refresh()
                                onEvent(MyInvitationUiEvent.Refresh)
                            },
                            modifier = Modifier.fillMaxSize()
                        ) {
                            PagingStateContent(
                                loadState = currentItems.loadState.source.refresh,
                                itemCount = currentItems.itemCount,
                                mediatorLoadState = currentItems.loadState.mediator?.refresh,
                                emptyComment = stringResource(R.string.label_myinvitation_empty),
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

                                                    onEvent(
                                                        MyInvitationUiEvent.ChangeSort(
                                                            isUpcoming = isUpcoming,
                                                            newSort = newDirection
                                                        )
                                                    )
                                                }
                                            )
                                        }
                                    }

                                    items(
                                        count = currentItems.itemCount,
                                        key = currentItems.itemKey { it.id }
                                    ) { index ->
                                        currentItems[index]?.let { invitation ->
                                            val isSelected =
                                                shouldHighlightSelected && invitation.id == selectedInvitationId
                                            val dDayLabel = when (val count = invitation.dDayCount) {
                                                null -> null
                                                0 -> stringResource(R.string.format_invitation_d_day_today)
                                                else -> stringResource(R.string.format_invitation_d_day, count)
                                            }

                                            InvitationListItem(
                                                imageUrl = invitation.thumbnailUrls.firstOrNull() ?: "",
                                                title = invitation.title,
                                                startTime = invitation.invitationDateTime,
                                                hostName = invitation.displayHostName,
                                                address = invitation.address,
                                                dDayText = dDayLabel,
                                                onClick = { onEvent(MyInvitationUiEvent.ClickInvitation(invitation.id)) },
                                                menuItems = persistentListOf(
                                                    MenuItem(
                                                        title = stringResource(R.string.txt_delete_invitation_title),
                                                        onClick = { onDeleteClick(invitation.id) }
                                                    )
                                                ),
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

            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(NachoSpacing.large)
                    ) {
                        Text(
                            text = stringResource(R.string.desc_not_logged_title),
                            style = NachoTheme.typography.headingSmallSemiBold,
                            color = NachoTheme.colorScheme.textPrimary,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = stringResource(R.string.desc_not_logged_content),
                            style = NachoTheme.typography.bodyLargeMedium,
                            color = NachoTheme.colorScheme.textSecondary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(NachoSpacing.large))

                        NachoButton(
                            onClick = { onEvent(MyInvitationUiEvent.ClickLogin) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = NachoSpacing.twoXLarge),
                            shape = RoundedCornerShape(NachoSpacing.medium),
                            contentPadding = PaddingValues(vertical = NachoSpacing.medium),
                        ) {
                            Text(
                                text = stringResource(R.string.txt_start_login),
                                style = NachoTheme.typography.bodyLargeSemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
