package com.andlife.nacho.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.navigation.compose.NavHost
import androidx.navigation.navDeepLink
import androidx.navigation.navOptions
import com.andlife.deeplink.DeepLinkManager
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.home.Setting
import com.andlife.home.homeNavGraph
import com.andlife.home.settingNavGraph
import com.andlife.invitation.invitationDetailNavGraph
import com.andlife.invitation.invitationNavGraph
import com.andlife.invitation_card.createCardByInvitationNavGraph
import com.andlife.invitation_card.createCardNavGraph
import com.andlife.invitation_card.updateCardNavGraph
import com.andlife.invitation_edit.addressSearchNavGraph
import com.andlife.invitation_edit.invitationCreateNavGraph
import com.andlife.invitation_edit.invitationEditNavGraph
import com.andlife.invitation_edit.invitationPreviewNavGraph
import com.andlife.login.Login
import com.andlife.login.loginNavGraph
import com.andlife.model.util.NavigationKeyConstant.CREATE_CARD_BY_INVITATION_ID
import com.andlife.model.util.NavigationKeyConstant.CREATE_THANKS_CARD
import com.andlife.model.util.NavigationKeyConstant.UPDATE_CARD
import com.andlife.myinvitation.myInvitationDetailNavGraph
import com.andlife.myinvitation.myInvitationNavGraph
import com.andlife.thanks_card.createThanksCardNavGraph
import com.andlife.thanks_card.updateThanksCardNavGraph
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
fun NachoNavHost(
    navigator: NachoNavigator,
    deepLinkManager: DeepLinkManager,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        containerColor = NachoTheme.colorScheme.backgroundPrimary,
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        bottomBar = {
            AnimatedVisibility(navigator.shouldShowBottomBar()) {
                InvitationBottomBar(
                    currentTab = navigator.currentTab,
                    tabs = navigator.mainBottomTabs.toImmutableList(),
                    onTabSelect = navigator::navigate,
                    modifier = Modifier,
                )
            }
        },
        modifier = modifier,
    ) { innerPadding ->
        NavHost(
            modifier = Modifier,
            navController = navigator.navController,
            startDestination = navigator.startDestination,
        ) {
            homeNavGraph(
                paddingValues = innerPadding,
                snackbarHostState = snackbarHostState,
                onNavigateToCreate = navigator::navigateToMyInvitationCreate,
                onNavigateToInvitationDetail = navigator::navigateToInvitationDetail,
                onNavigateToMyInvitationDetail = navigator::navigateToMyInvitationDetail,
                onNavigateToSetting = navigator::navigateToSetting,
            )

            settingNavGraph(
                onNavigateBack = navigator::navigatePopBackStack,
                onNavigateToLogin = {
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
                paddingValues = innerPadding,
                onNavigateToDetail = navigator::navigateToInvitationDetail,
                snackbarHostState = snackbarHostState
            )

            invitationDetailNavGraph(
                deepLinks = navDeepLink { uriPattern = deepLinkManager.getKakaoDeepLinkPattern() },
                onNavigateBack = navigator::navigatePopBackStack,
                onNavigateToLogin = {
                    val navOptions = navOptions {
                        popUpTo(navigator.navController.graph.id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                    navigator.navigateToLogin(navOptions)
                }
            )

            myInvitationNavGraph(
                 snackbarHostState = snackbarHostState,
                paddingValues = innerPadding,
                onNavigateToCreate = navigator::navigateToMyInvitationCreate,
                onNavigateToDetail = navigator::navigateToMyInvitationDetail,
                onNavigateToLogin = {
                    val navOptions = navOptions {
                        popUpTo(navigator.navController.graph.id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                    navigator.navigateToLogin(navOptions)
                }
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
                onNavigateToInvitationDetail = navigator::navigateToMyInvitationDetailByCreate
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
                    navigator.navController.previousBackStackEntry?.savedStateHandle[CREATE_CARD_BY_INVITATION_ID] =
                        true
                    navigator.navigatePopBackStack()
                }
            )

            updateCardNavGraph(
                onBackClick = navigator::navigatePopBackStack,
                onSuccessCreateCard = {
                    navigator.navController.previousBackStackEntry?.savedStateHandle[UPDATE_CARD] = true
                    navigator.navigatePopBackStack()
                }
            )

            loginNavGraph()

            createThanksCardNavGraph(
                onSuccessfulCreate = {
                    navigator.navController.previousBackStackEntry?.savedStateHandle[CREATE_THANKS_CARD] = true
                    navigator.navigatePopBackStack()
                },
                onBackClick = navigator::navigatePopBackStack,
            )

            updateThanksCardNavGraph(
                onSuccessfulUpdate = {
                    navigator.navController.previousBackStackEntry?.savedStateHandle[UPDATE_CARD] = true
                    navigator.navigatePopBackStack()
                },
                onBackClick = navigator::navigatePopBackStack
            )
        }
    }
}

@Composable
private fun InvitationBottomBar(
    currentTab: MainBottomTab?,
    tabs: ImmutableList<MainBottomTab>,
    onTabSelect: (MainBottomTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(
        modifier = modifier,
        containerColor = NachoTheme.colorScheme.backgroundPrimary,
    ) {
        tabs.forEach { tab ->
            NavigationBarItem(
                selected = currentTab == tab,
                onClick = { onTabSelect(tab) },
                icon = {
                    Icon(
                        ImageVector.vectorResource(tab.iconResId),
                        stringResource(tab.labelResId),
                    )
                },
                label = {
                    Text(
                        text = stringResource(tab.labelResId),
                        style = NachoTheme.typography.bodySmallMedium,
                    )
                },
                colors =
                    NavigationBarItemDefaults.colors(
                        selectedIconColor = NachoTheme.colorScheme.brandPrimary,
                        unselectedIconColor = NachoTheme.colorScheme.textTertiary,
                        selectedTextColor = NachoTheme.colorScheme.brandPrimary,
                        unselectedTextColor = NachoTheme.colorScheme.textTertiary,
                        indicatorColor = Color.Transparent,
                    ),
            )
        }
    }
}

@PreviewTheme
@Composable
private fun InvitationBottomBarPreview() {
    NachoTheme {
        InvitationBottomBar(
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
