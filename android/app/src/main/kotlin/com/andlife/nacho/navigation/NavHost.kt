package com.andlife.nacho.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.WideNavigationRailDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItem
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.navDeepLink
import androidx.navigation.navOptions
import com.andlife.deeplink.DeepLinkManager
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.domain.util.AnalyticsEvent
import com.andlife.domain.util.RefreshEventHub
import com.andlife.home.homeNavGraph
import com.andlife.invitation.invitationDetailNavGraph
import com.andlife.invitation.invitationNavGraph
import com.andlife.invitation_card.createCardByInvitationNavGraph
import com.andlife.invitation_card.createCardNavGraph
import com.andlife.invitation_card.updateCardNavGraph
import com.andlife.invitation_edit.addressSearchNavGraph
import com.andlife.invitation_edit.invitationCreateNavGraph
import com.andlife.invitation_edit.invitationEditNavGraph
import com.andlife.invitation_edit.invitationPreviewNavGraph
import com.andlife.login.loginNavGraph
import com.andlife.myinvitation.myInvitationDetailNavGraph
import com.andlife.myinvitation.myInvitationNavGraph
import com.andlife.nacho.LocalAnalyticsLogger
import com.andlife.setting.settingNavGraph
import com.andlife.thanks_card.createThanksCardNavGraph
import com.andlife.thanks_card.updateThanksCardNavGraph
import com.andlife.ui.util.LocalNavigationSuiteState
import com.andlife.ui.util.isNavigationBar
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
fun NachoNavHost(
    navigator: NachoNavigator,
    deepLinkManager: DeepLinkManager,
    modifier: Modifier = Modifier,
) {
    val suiteState = LocalNavigationSuiteState.current
    val currentDestination = navigator.currentDestination
    val navSuiteType =
        NavigationSuiteScaffoldDefaults.navigationSuiteType(currentWindowAdaptiveInfo())
    val snackbarHostState = remember { SnackbarHostState() }
    val analyticsLogger = LocalAnalyticsLogger.current

    LaunchedEffect(currentDestination, navSuiteType) {
        navigator.navController.currentDestination?.route?.let { route ->
            analyticsLogger.logEvent(AnalyticsEvent.ScreenView(route))
        }
        val isBottomTab = MainBottomTab.entries.find { tab ->
            currentDestination?.hasRoute(tab.route) == true
        }
        when {
            navSuiteType.isNavigationBar && isBottomTab != null -> {
                if (suiteState.currentValue == NavigationSuiteScaffoldValue.Hidden) suiteState.show()
            }

            navSuiteType.isNavigationBar && isBottomTab == null -> {
                if (suiteState.currentValue == NavigationSuiteScaffoldValue.Visible) suiteState.hide()
            }

            !navSuiteType.isNavigationBar && isBottomTab == null -> {
                if (suiteState.currentValue == NavigationSuiteScaffoldValue.Visible) suiteState.hide()
            }

            !navSuiteType.isNavigationBar && isBottomTab != null -> {
                if (suiteState.currentValue == NavigationSuiteScaffoldValue.Hidden) suiteState.show()
            }
        }
    }

    NavigationSuiteScaffold(
        navigationItems = {
            NachoBottomBar(
                currentTab = navigator.currentTab,
                tabs = navigator.mainBottomTabs.toImmutableList(),
                onTabSelect = navigator::navigate,
                modifier = Modifier,
            )
        },
        containerColor = NachoTheme.colorScheme.backgroundPrimary,
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            shortNavigationBarContainerColor = NachoTheme.colorScheme.backgroundPrimary,
            navigationBarContainerColor = NachoTheme.colorScheme.backgroundPrimary,
            navigationRailContentColor = NachoTheme.colorScheme.backgroundPrimary,
            navigationDrawerContentColor = NachoTheme.colorScheme.backgroundPrimary,
            wideNavigationRailColors = WideNavigationRailDefaults.colors(
                containerColor = NachoTheme.colorScheme.backgroundPrimary
            ),
        ),
        state = suiteState
    ) {
        Scaffold(
            snackbarHost = {
                SnackbarHost(snackbarHostState)
            }
        ) { innerPadding ->
            NavHost(
                modifier = Modifier,
                navController = navigator.navController,
                startDestination = navigator.startDestination,
            ) {
                homeNavGraph(
                    snackbarHostState = snackbarHostState,
                    onNavigateToCreate = navigator::navigateToMyInvitationCreate,
                    onNavigateToLogin = { navigator.navigateToLogin() },
                    onNavigateToInvitationDetail = navigator::navigateToInvitationDetail,
                    onNavigateToMyInvitationDetail = navigator::navigateToMyInvitationDetail,
                    onNavigateToSetting = navigator::navigateToSetting,
                )

                settingNavGraph(
                    onNavigateBack = navigator::navigatePopBackStack,
                    onNavigateToLogin = {
                        navigator.navigateToLogin()
                    },
                    onSignedOut = {
                        val navOptions = navOptions {
                            popUpTo(navigator.navController.graph.id) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                        navigator.navigateToLogin(navOptions)
                    },
                    onLogout = {
                        val navOptions = navOptions {
                            popUpTo(navigator.navController.graph.id) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                        navigator.navigateToLogin(navOptions)
                    }
                )

                invitationNavGraph(
                    deepLinks = navDeepLink { uriPattern = deepLinkManager.getKakaoDeepLinkPattern() },
                    onNavigateToLogin = { navigator.navigateToLogin() },
                    snackbarHostState = snackbarHostState
                )

                invitationDetailNavGraph(
                    deepLinks = navDeepLink { uriPattern = deepLinkManager.getKakaoDeepLinkPattern() },
                    onNavigateBack = navigator::navigatePopBackStack,
                    onNavigateToLogin = { navigator.navigateToLogin() }
                )

                myInvitationNavGraph(
                    snackbarHostState = snackbarHostState,
                    onNavigateToCreate = navigator::navigateToMyInvitationCreate,
                    onNavigateToLogin = {
                        navigator.navigateToLogin()
                    },
                    onNavigateToEditInvitation = navigator::navigateToMyInvitationEdit,
                    onNavigateToEditCard = navigator::navigateToUpdateCard,
                    onNavigateToCreateCard = navigator::navigateToCreateCardByInvitation,
                    onNavigateToCreateThanksCard = navigator::navigateToCreateThanksCard,
                    onNavigateToUpdateThanksCard = navigator::navigateToUpdateThanksCard
                )

                myInvitationDetailNavGraph(
                    onNavigateBack = navigator::navigatePopBackStack,
                    onNavigateToLogin = {
                        val navOptions = navOptions {
                            popUpTo(navigator.navController.graph.id) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                        navigator.navigateToLogin(navOptions)
                    },
                    onNavigateToEditInvitation = navigator::navigateToMyInvitationEdit,
                    onNavigateToEditCard = navigator::navigateToUpdateCard,
                    onNavigateToCreateCard = navigator::navigateToCreateCardByInvitation,
                    onNavigateToCreateThanksCard = navigator::navigateToCreateThanksCard,
                    onNavigateToUpdateThanksCard = navigator::navigateToUpdateThanksCard
                )

                invitationCreateNavGraph(
                    onNavigateToAddressSearch = navigator::navigateToAddressSearch,
                    onNavigateToPreview = navigator::navigateToInvitationPreview,
                    onNavigateBack = navigator::navigatePopBackStack,
                    onNavigateCreateCard = navigator::navigateToCreateCard,
                    onNavigateToInvitationDetail = { id ->
                        navigator.navigatePopBackStack()
                    }
                )

                invitationEditNavGraph(
                    navController = navigator.navController,
                    onNavigateToAddressSearch = navigator::navigateToAddressSearch,
                    onNavigateBack = navigator::navigatePopBackStack,
                )

                addressSearchNavGraph(
                    navController = navigator.navController,
                    onNavigateBack = navigator::navigatePopBackStack,
                )

                invitationPreviewNavGraph(
                    navController = navigator.navController,
                    onNavigateBack = navigator::navigatePopBackStack,
                )

                createCardNavGraph(
                    onBackClick = navigator::navigatePopBackStack
                )

                createCardByInvitationNavGraph(
                    onBackClick = navigator::navigatePopBackStack,
                    onSuccessCreateCard = {
                        navigator.navigatePopBackStack()
                    }
                )

                updateCardNavGraph(
                    onBackClick = navigator::navigatePopBackStack,
                    onSuccessCreateCard = {
                        navigator.navigatePopBackStack()
                    }
                )

                loginNavGraph()

                createThanksCardNavGraph(
                    onSuccessfulCreate = navigator::navigatePopBackStack,
                    onBackClick = navigator::navigatePopBackStack,
                )

                updateThanksCardNavGraph(
                    onSuccessfulUpdate = navigator::navigatePopBackStack,
                    onBackClick = navigator::navigatePopBackStack
                )
            }
        }
    }
}

@Composable
private fun NachoBottomBar(
    currentTab: MainBottomTab?,
    tabs: ImmutableList<MainBottomTab>,
    onTabSelect: (MainBottomTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    tabs.forEach { tab ->
        NavigationSuiteItem(
            selected = currentTab == tab,
            onClick = { onTabSelect(tab) },
            label = {
                Text(
                    text = stringResource(id = tab.labelResId),
                    style = NachoTheme.typography.bodySmallMedium,
                )
            },
            icon = {
                Icon(
                    imageVector = ImageVector.vectorResource(id = tab.iconResId),
                    contentDescription = stringResource(id = tab.labelResId),
                )
            },
            colors = NavigationItemColors(
                selectedIconColor = NachoTheme.colorScheme.brandPrimary,
                selectedTextColor = NachoTheme.colorScheme.brandPrimary,
                selectedIndicatorColor = NachoTheme.colorScheme.backgroundPrimary,
                unselectedIconColor = NachoTheme.colorScheme.textTertiary,
                unselectedTextColor = NachoTheme.colorScheme.textTertiary,
                disabledIconColor = NachoTheme.colorScheme.textTertiary,
                disabledTextColor = NachoTheme.colorScheme.textTertiary,
            )
        )
    }
}

@PreviewTheme
@Composable
private fun NachoBottomBarPreview() {
    NachoTheme {
        NachoBottomBar(
            currentTab = MainBottomTab.INVITATION,
            tabs =
                listOf(
                    MainBottomTab.HOME,
                    MainBottomTab.INVITATION,
                    MainBottomTab.MY_INVITATION,
                ).toImmutableList(),
            onTabSelect = {},
        )
    }
}
