package com.andlife.invitation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.andlife.designsystem.component.dialog.NachoDialog
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.domain.model.invitation.SortDirection
import com.andlife.invitation.model.InvitationSideEffect
import com.andlife.invitation.model.InvitationUiEvent
import com.andlife.invitation.model.InvitationUiState
import com.andlife.invitation.viewmodel.InvitationViewModel
import com.andlife.model.invitation.InvitationSummaryUiModel
import com.andlife.ui.R
import com.andlife.ui.component.GenericTabRow
import com.andlife.ui.component.invitation.InvitationListHeader
import com.andlife.ui.component.invitation.InvitationTopBar
import com.andlife.ui.component.listitem.InvitationListItem
import com.andlife.ui.component.listitem.MenuItem
import com.andlife.ui.component.loading.InvitationLoadingIndicator
import com.andlife.ui.component.paging.PagingStateContent
import com.andlife.ui.util.collectWithLifecycle
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

@Composable
fun InvitationRoute(
    snackbarHostState: SnackbarHostState,
    onNavigateToDetail: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InvitationViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val upcomingItems = viewModel.upcomingInvitationPagingFlow.collectAsLazyPagingItems()
    val pastItems = viewModel.pastInvitationPagingFlow.collectAsLazyPagingItems()

    var invitationIdToLeave by remember { mutableStateOf<Long?>(null) }

    val scope = rememberCoroutineScope()

    val refreshFailureMessage = stringResource(R.string.msg_refresh_failure)
    val leaveSuccessMessage = stringResource(R.string.msg_leave_success)
    val leaveFailureMessage = stringResource(R.string.msg_leave_failure)

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
        NachoDialog(
            onDismiss = { invitationIdToLeave = null }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(NachoSpacing.xLarge)
            ) {
                Text(
                    text = stringResource(R.string.txt_leave_invitation_title),
                    style = NachoTheme.typography.headingSmallSemiBold,
                    color = NachoTheme.colorScheme.textPrimary
                )

                Text(
                    text = stringResource(R.string.txt_leave_invitation_message),
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
                        onClick = { invitationIdToLeave = null }
                    ) {
                        Text(
                            text = stringResource(R.string.txt_cancel),
                            color = NachoTheme.colorScheme.textSecondary
                        )
                    }

                    Spacer(modifier = Modifier.padding(horizontal = NachoSpacing.small))

                    TextButton(
                        onClick = {
                            viewModel.leaveInvitation(id)
                            invitationIdToLeave = null
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

    InvitationScreen(
        uiState = uiState,
        upcomingItems = upcomingItems,
        pastItems = pastItems,
        modifier = modifier,
        onEvent = viewModel::onEvent,
        onLeaveClick = { invitationIdToLeave = it }
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
    modifier: Modifier = Modifier
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
                        loadState = currentItems.loadState.refresh,
                        itemCount = currentItems.itemCount,
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
                                        onClick = { onEvent(InvitationUiEvent.ClickInvitation(invitation.id)) },
                                        menuItems = persistentListOf(
                                            MenuItem(
                                                title = stringResource(R.string.txt_leave_invitation_title),
                                                onClick = { onLeaveClick(invitation.id) }
                                            )
                                        )
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
