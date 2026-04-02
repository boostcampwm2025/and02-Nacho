package com.andlife.myinvitation.screen.listdetail

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.myinvitation.MyInvitationDetail
import com.andlife.myinvitation.MyInvitationPlaceholder
import com.andlife.myinvitation.screen.detail.MyInvitationDetailRoute
import com.andlife.myinvitation.screen.detail.MyInvitationPlaceholderScreen
import com.andlife.myinvitation.screen.list.MyInvitationRoute
import com.andlife.myinvitation.viewmodel.MyInvitationDetailViewModel
import com.andlife.myinvitation.viewmodel.MyInvitationGuestBookViewModel
import com.andlife.myinvitation.viewmodel.MyInvitationListDetailViewModel
import com.andlife.ui.scope.DetailPaneScopedViewModel
import com.andlife.ui.util.LocalNavigationSuiteState
import com.andlife.ui.util.isNavigationBar
import kotlinx.coroutines.launch

@Composable
fun MyInvitationListDetailRoute(
    snackbarHostState: SnackbarHostState,
    onNavigateToLogin: () -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToEditInvitation: (Long) -> Unit,
    onNavigateToEditCard: (Long) -> Unit,
    onNavigateToCreateCard: (Long) -> Unit,
    onNavigateToCreateThanksCard: (Long) -> Unit,
    onNavigateToUpdateThanksCard: (Long) -> Unit,
    viewModel: MyInvitationListDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.initialInvitationId.collectAsStateWithLifecycle()

    MyInvitationListDetailScreen(
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
private fun MyInvitationListDetailScreen(
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
                            DetailPaneScopedViewModel {
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
