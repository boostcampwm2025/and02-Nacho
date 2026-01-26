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
import com.andlife.deeplink.DeepLinkManager
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.home.homeNavGraph
import com.andlife.home.settingNavGraph
import com.andlife.invitation.invitationDetailNavGraph
import com.andlife.invitation.invitationNavGraph
import com.andlife.invitation_card.createCardByInvitationNavGraph
import com.andlife.invitation_card.createCardNavGraph
import com.andlife.invitation_card.updateCardNavGraph
import com.andlife.invitation_edit.addressSearchNavGraph
import com.andlife.invitation_edit.myInvitationCreateNavGraph
import com.andlife.invitation_edit.myInvitationEditNavGraph
import com.andlife.model.util.NavigationKeyConstant.CREATE_CARD_BY_INVITATION_ID
import com.andlife.model.util.NavigationKeyConstant.UPDATE_CARD
import com.andlife.myinvitation.myInvitationDetailNavGraph
import com.andlife.myinvitation.myInvitationNavGraph
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
            )

            invitationNavGraph(
                paddingValues = innerPadding,
                onNavigateToDetail = navigator::navigateToInvitationDetail,
            )

            invitationDetailNavGraph(
                deepLinks = navDeepLink { uriPattern = deepLinkManager.getKakaoDeepLinkPattern() },
                onNavigateBack = navigator::navigatePopBackStack,
            )

            myInvitationNavGraph(
                paddingValues = innerPadding,
                onNavigateToCreate = navigator::navigateToMyInvitationCreate,
                onNavigateToDetail = navigator::navigateToMyInvitationDetail,
            )

            myInvitationDetailNavGraph(
                onNavigateBack = navigator::navigatePopBackStack,
                onNavigateToEditInvitation = navigator::navigateToMyInvitationEdit,
                onNavigateToEditCard = navigator::navigateToUpdateCard,
                onNavigateToCreateCard = navigator::navigateToCreateCardByInvitation
            )

            myInvitationCreateNavGraph(
                onNavigateToAddressSearch = navigator::navigateToAddressSearch,
                onNavigateBack = navigator::navigatePopBackStack,
                onNavigateCreateCard = navigator::navigateToCreateCard,
                onNavigateToInvitationDetail = navigator::navigateToMyInvitationDetailByCreate
            )

            myInvitationEditNavGraph(
                onNavigateToAddressSearch = navigator::navigateToAddressSearch,
                onNavigateBack = navigator::navigatePopBackStack,
                onNavigateCreateCard = navigator::navigateToCreateCard,
                onNavigateToInvitationDetail = navigator::navigateToMyInvitationDetail
            )

            addressSearchNavGraph(
                navController = navigator.navController,
                onNavigateBack = navigator::navigatePopBackStack,
            )

            createCardNavGraph(
                onBackClick = navigator::navigatePopBackStack
            )

            createCardByInvitationNavGraph(
                onBackClick = navigator::navigatePopBackStack,
                onSuccessCreateCard = {
                    navigator.navController.previousBackStackEntry?.savedStateHandle[CREATE_CARD_BY_INVITATION_ID] = true
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
