package com.andlife.invitation.screen.listdetail

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.invitation.InvitationDetail
import com.andlife.invitation.InvitationPlaceholder
import com.andlife.invitation.screen.list.InvitationRoute
import com.andlife.invitation.screen.detail.InvitationDetailRoute
import com.andlife.invitation.screen.detail.InvitationPlaceholderScreen
import com.andlife.invitation.viewmodel.Invitation2PaneUiState
import com.andlife.invitation.viewmodel.Invitation2PaneViewModel
import com.andlife.invitation.viewmodel.InvitationDetailViewModel
import com.andlife.invitation.viewmodel.InvitationGuestBookViewModel
import com.andlife.ui.scope.DetailPaneScopedViewModel
import com.andlife.ui.util.LocalNavigationSuiteState
import com.andlife.ui.util.isNavigationBar
import kotlinx.coroutines.launch

@Composable
fun InvitationListDetailRoute(
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
        onInvitationClick = viewModel::onInvitationClick,
        modifier = modifier,
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
        onInvitationClick(id)
        invitationRoute = InvitationDetail(id)
        coroutineScope.launch {
            if (navSuiteType.isNavigationBar && suiteState.currentValue == NavigationSuiteScaffoldValue.Visible) suiteState.hide()
            listDetailNavigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
        }
    }

    LaunchedEffect(Unit) {
        if (!isConsumeDeepLink && uiState.isFromDeeplLink) {
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
                            DetailPaneScopedViewModel {
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
                                        factory.create(route.id, uiState.isFromDeeplLink)
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
