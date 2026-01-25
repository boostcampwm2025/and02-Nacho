package com.andlife.myinvitation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.domain.model.invitation.SortDirection
import com.andlife.model.invitation.InvitationSummaryUiModel
import com.andlife.myinvitation.model.MyInvitationSideEffect
import com.andlife.myinvitation.model.MyInvitationUiEvent
import com.andlife.myinvitation.model.MyInvitationUiState
import com.andlife.myinvitation.viewmodel.MyInvitationViewModel
import com.andlife.ui.R
import com.andlife.ui.component.GenericTabRow
import com.andlife.ui.component.invitation.InvitationListHeader
import com.andlife.ui.component.invitation.InvitationTopBar
import com.andlife.ui.component.listitem.InvitationListItem
import com.andlife.ui.component.loading.InvitationLoadingIndicator
import com.andlife.ui.component.paging.PagingStateContent
import com.andlife.ui.util.collectWithLifecycle
import kotlinx.collections.immutable.toImmutableList

@Composable
fun MyInvitationRoute(
    onNavigateToCreate: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyInvitationViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val upcomingItems = viewModel.upcomingMyInvitationPagingFlow.collectAsLazyPagingItems()
    val pastItems = viewModel.pastMyInvitationPagingFlow.collectAsLazyPagingItems()

    viewModel.effectFlow.collectWithLifecycle { effect ->
        when (effect) {
            is MyInvitationSideEffect.NavigateToDetail -> onNavigateToDetail(effect.id)
            is MyInvitationSideEffect.RefreshFailure -> { /* TODO : 에러 스낵바 처리 */ }
            is MyInvitationSideEffect.NavigateToCreate -> onNavigateToCreate()
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

    MyInvitationScreen(
        uiState = uiState,
        upcomingItems = upcomingItems,
        pastItems = pastItems,
        modifier = modifier,
        onEvent = viewModel::onEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyInvitationScreen(
    uiState: MyInvitationUiState,
    upcomingItems: LazyPagingItems<InvitationSummaryUiModel>,
    pastItems: LazyPagingItems<InvitationSummaryUiModel>,
    modifier: Modifier = Modifier,
    onEvent: (MyInvitationUiEvent) -> Unit
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onEvent(MyInvitationUiEvent.ClickCreate) },
                containerColor = NachoTheme.colorScheme.brandPrimary,
                contentColor = NachoTheme.colorScheme.brandOnPrimary,
                modifier = Modifier.padding(NachoSpacing.medium)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add_24),
                    contentDescription = stringResource(R.string.desc_invitation_create)
                )
            }
        },
        contentWindowInsets = WindowInsets(),
        containerColor = NachoTheme.colorScheme.backgroundPrimary,
    ) { paddingValues ->
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
                                        totalCount = currentItems.itemCount,
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

                                            onEvent(MyInvitationUiEvent.ChangeSort(
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
                                        onClick = { onEvent(MyInvitationUiEvent.ClickInvitation(invitation.id)) },
                                        onMoreClick = {}
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
