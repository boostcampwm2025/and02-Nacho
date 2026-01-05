package com.andlife.invitationzzang.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.navigation.compose.NavHost
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.InvitationTheme
import com.andlife.home.homeNavGraph
import com.andlife.invitation.invitationDetailNavGraph
import com.andlife.invitation.invitationNavGraph
import com.andlife.invitation_edit.addressSearchNavGraph
import com.andlife.invitation_edit.myInvitationCreateNavGraph
import com.andlife.myinvitation.myInvitationNavGraph
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
fun InvitationNavHost(
    navigator: InvitationNavigator,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        containerColor = InvitationTheme.colorScheme.backgroundPrimary,
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
            homeNavGraph(innerPadding)

            invitationNavGraph(
                paddingValues = innerPadding,
                onNavigateToDetail = navigator::navigateToInvitationDetail,
            )

            invitationDetailNavGraph()

            myInvitationNavGraph(
                paddingValues = innerPadding,
                onNavigateToCreate = navigator::navigateToMyInvitationCreate,
            )

            myInvitationCreateNavGraph(
                onNavigateToAddressSearch = navigator::navigateToAddressSearch,
                onNavigateBack = navigator::navigatePopBackStack,
            )

            addressSearchNavGraph(
                navController = navigator.navController,
                onNavigateBack = navigator::navigatePopBackStack,
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
        containerColor = InvitationTheme.colorScheme.backgroundPrimary,
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
                        style = InvitationTheme.typography.bodySmallMedium,
                    )
                },
                colors =
                    NavigationBarItemDefaults.colors(
                        selectedIconColor = InvitationTheme.colorScheme.brandPrimary,
                        unselectedIconColor = InvitationTheme.colorScheme.textTertiary,
                        selectedTextColor = InvitationTheme.colorScheme.brandPrimary,
                        unselectedTextColor = InvitationTheme.colorScheme.textTertiary,
                        indicatorColor = Color.Transparent,
                    ),
            )
        }
    }
}

@PreviewTheme
@Composable
private fun InvitationBottomBarPreview() {
    InvitationTheme {
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
