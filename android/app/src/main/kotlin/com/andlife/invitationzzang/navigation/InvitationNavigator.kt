package com.andlife.invitationzzang.navigation

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
import com.andlife.myinvitation.navigateToMyInvitation

@Stable
class InvitationNavigator(
    val navController: NavHostController
) {

    val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    val startDestination = Home

    val mainBottomTabs = MainBottomTab.entries

    val currentTab: MainBottomTab?
        @Composable get() = MainBottomTab.entries.find { tab ->
            currentDestination?.hasRoute(tab.route) == true
        }

    @Composable
    fun shouldShowBottomBar(): Boolean = MainBottomTab.entries.any {tab ->
        currentDestination?.hasRoute(tab.route) == true
    }

    fun navigate(tab: MainBottomTab) {
        val navOptions = navOptions {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }

        when (tab) {
            MainBottomTab.HOME -> { navController.navigateToHome(navOptions) }
            MainBottomTab.INVITATION -> { navController.navigateToInvitation(navOptions) }
            MainBottomTab.MY_INVITATION -> { navController.navigateToMyInvitation(navOptions) }
        }
    }

    // 초대장 탭에서 -> 초대 상세로 가는 함수
    fun navigateToInvitationDetail(id: Long) {
    }

    fun navigateToMyInvitationDetail(id: Long) {
    }

    fun popBackStack() {
        navController.popBackStack()
    }
}

@Composable
internal fun rememberInvitationNavigator(
    navController: NavHostController = rememberNavController(),
): InvitationNavigator = remember(navController) {
    InvitationNavigator(navController)
}



