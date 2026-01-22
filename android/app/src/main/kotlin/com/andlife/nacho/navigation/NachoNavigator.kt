package com.andlife.nacho.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.andlife.home.Home
import com.andlife.home.navigateToHome
import com.andlife.invitation.navigateToInvitation
import com.andlife.invitation.navigateToInvitationDetail
import com.andlife.invitation_card.navigateToCardEditor
import com.andlife.invitation_card.navigateToCreateCardByInvitation
import com.andlife.invitation_card.navigateToUpdateCard
import com.andlife.invitation_edit.MyInvitationCreate
import com.andlife.invitation_edit.navigateToAddressSearch
import com.andlife.invitation_edit.navigateToMyInvitationCreate
import com.andlife.myinvitation.navigateToMyInvitation
import com.andlife.myinvitation.navigateToMyInvitationDetail

@Stable
class NachoNavigator(
    val navController: NavHostController,
) {
    val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    val startDestination = Home

    val mainBottomTabs = MainBottomTab.entries

    val currentTab: MainBottomTab?
        @Composable get() =
            MainBottomTab.entries.find { tab ->
                currentDestination?.hasRoute(tab.route) == true
            }

    @Composable
    fun shouldShowBottomBar(): Boolean =
        MainBottomTab.entries.any { tab ->
            currentDestination?.hasRoute(tab.route) == true
        }

    fun navigate(tab: MainBottomTab) {
        val navOptions =
            navOptions {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }

        when (tab) {
            MainBottomTab.HOME -> {
                navController.navigateToHome(navOptions)
            }

            MainBottomTab.INVITATION -> {
                navController.navigateToInvitation(navOptions)
            }

            MainBottomTab.MY_INVITATION -> {
                navController.navigateToMyInvitation(navOptions)
            }
        }
    }

    fun navigateToInvitationDetail(id: Long) {
        navController.navigateToInvitationDetail(
            id = id,
            navOptions = navOptions { launchSingleTop = true },
        )
    }

    fun navigateToMyInvitationCreate() {
        navController.navigateToMyInvitationCreate(
            navOptions = navOptions { launchSingleTop = true },
        )
    }

    fun navigateToMyInvitationDetail(id: Long) {
        navController.navigateToMyInvitationDetail(
            id = id,
            navOptions = navOptions { launchSingleTop = true },
        )
    }

    fun navigateToMyInvitationDetailByCreate(id: Long) {
        navController.navigateToMyInvitationDetail(
            id = id,
            navOptions = navOptions {
                launchSingleTop = true
                popUpTo<MyInvitationCreate> {
                    inclusive = true
                }
            },
        )
    }

    fun navigateToAddressSearch() {
        navController.navigateToAddressSearch(navOptions = navOptions { launchSingleTop = true })
    }

    fun navigateToCreateCard() {
        navController.navigateToCardEditor()
    }

    fun navigateToCreateCardByInvitation(id: Long) {
        navController.navigateToCreateCardByInvitation(id)
    }

    fun navigateToUpdateCard(cardId: Long) {
        navController.navigateToUpdateCard(cardId)
    }

    fun navigatePopBackStack() {
        navController.popBackStack()
    }
}

@Composable
internal fun rememberInvitationNavigator(
    navController: NavHostController = rememberNavController(),
): NachoNavigator =
    remember(navController) {
        NachoNavigator(navController)
    }
