package com.andlife.invitation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.invitation.model.InvitationSideEffect
import com.andlife.invitation.model.InvitationUiEvent
import com.andlife.invitation.model.InvitationUiState
import com.andlife.invitation.viewmodel.InvitationViewModel
import com.andlife.model.invitation.InvitationSummaryUiModel
import com.andlife.ui.component.GenericTabRow
import com.andlife.ui.component.listitem.InvitationListItem
import com.andlife.ui.util.collectWithLifecycle
import kotlinx.collections.immutable.persistentListOf

@Composable
fun InvitationRoute(
    onNavigateToDetail: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InvitationViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val upcomingItems = viewModel.upcomingInvitationPagingFlow.collectAsLazyPagingItems()
    val pastItems = viewModel.pastInvitationPagingFlow.collectAsLazyPagingItems()

    viewModel.effectFlow.collectWithLifecycle { effect ->
        when (effect) {
            is InvitationSideEffect.NavigateToDetail -> onNavigateToDetail(effect.id)
            is InvitationSideEffect.RefreshFailure -> { /* TODO : 에러 스낵바 처리 */ }
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

    InvitationScreen(
        uiState = uiState,
        upcomingItems = upcomingItems,
        pastItems = pastItems,
        modifier = modifier,
        onEvent = viewModel::onEvent,
    )
}

@Composable
private fun InvitationScreen(
    uiState: InvitationUiState,
    upcomingItems: LazyPagingItems<InvitationSummaryUiModel>,
    pastItems: LazyPagingItems<InvitationSummaryUiModel>,
    modifier: Modifier = Modifier,
    onEvent: (InvitationUiEvent) -> Unit
) {
    val tabs = persistentListOf("다가오는 초대", "지난 초대")

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {},
        containerColor = NachoTheme.colorScheme.backgroundPrimary,
    ) { paddingValues ->
        GenericTabRow(
            tabs = tabs,
            modifier = Modifier.padding(paddingValues),
            content = { pageIndex ->
                LaunchedEffect(pageIndex) {
                    onEvent(InvitationUiEvent.SelectTab(pageIndex))
                }

                val currentItems = if (pageIndex == 0) upcomingItems else pastItems

                PullToRefreshBox(
                    isRefreshing = uiState.isRefreshing,
                    onRefresh = {
                        currentItems.refresh()
                        onEvent(InvitationUiEvent.Refresh)
                    },
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(NachoSpacing.large),
                        verticalArrangement = Arrangement.spacedBy(NachoSpacing.medium)
                    ) {
                        items(
                            count = currentItems.itemCount,
                            key = currentItems.itemKey { it.id }

                        ) { index ->
                            currentItems[index]?.let { invitation ->
                                InvitationListItem(
                                    imageUrl = invitation.thumbnailUrls.first(),
                                    title = invitation.title,
                                    startTime = invitation.invitationDateTime,
                                    hostName = invitation.displayHostName,
                                    address = invitation.address,
                                    dDayText = invitation.dDayCount.toString(),
                                    onClick = { onEvent(InvitationUiEvent.ClickInvitation(invitation.id)) }
                                )
                            }
                        }
                    }
                }
            }
        )
    }
}
