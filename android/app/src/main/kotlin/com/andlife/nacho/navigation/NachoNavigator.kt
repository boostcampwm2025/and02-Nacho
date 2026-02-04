package com.andlife.nacho.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.andlife.home.Home
import com.andlife.home.navigateToHome
import com.andlife.home.navigateToSetting
import com.andlife.invitation.navigateToInvitation
import com.andlife.invitation.navigateToInvitationDetail
import com.andlife.invitation_card.navigateToCardEditor
import com.andlife.invitation_card.navigateToCreateCardByInvitation
import com.andlife.invitation_card.navigateToUpdateCard
import com.andlife.invitation_edit.InvitationCreate
import com.andlife.invitation_edit.navigateToAddressSearch
import com.andlife.invitation_edit.navigateToInvitationCreate
import com.andlife.invitation_edit.navigateToInvitationEdit
import com.andlife.invitation_edit.navigateToInvitationPreview
import com.andlife.login.Login
import com.andlife.login.navigateToLogin
import com.andlife.myinvitation.navigateToMyInvitation
import com.andlife.myinvitation.navigateToMyInvitationDetail
import com.andlife.thanks_card.navigateToCreateThanksCard
import com.andlife.thanks_card.navigateToUpdateThanksCard
import kotlin.reflect.KClass

@Stable
class NachoNavigator(
    val navController: NavHostController,
    val startDestination: KClass<*> = Login::class
) {
    val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

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
                popUpTo(Home) {
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

    fun navigateToHome(navOptions: NavOptions) {
        navController.navigateToHome(navOptions)
    }

    fun navigateToSetting() {
        navController.navigateToSetting(
            navOptions = navOptions { launchSingleTop = true },
        )
    }

    fun navigateToInvitationDetail(id: Long) {
        navController.navigateToInvitationDetail(
            id = id,
            navOptions = navOptions { launchSingleTop = true },
        )
    }

    fun navigateToMyInvitationCreate() {
        navController.navigateToInvitationCreate(
            navOptions = navOptions { launchSingleTop = true },
        )
    }

    fun navigateToMyInvitationEdit(id: Long) {
        navController.navigateToInvitationEdit(
            id = id,
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
                popUpTo<InvitationCreate> {
                    inclusive = true
                }
            },
        )
    }

    fun navigateToAddressSearch() {
        navController.navigateToAddressSearch(navOptions = navOptions { launchSingleTop = true })
    }

    fun navigateToInvitationPreview() {
        navController.navigateToInvitationPreview(navOptions = navOptions { launchSingleTop = true })
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

    fun navigateToLogin(navOptions: NavOptions? = null) {
        navController.navigateToLogin(navOptions)
    }

    fun navigatePopBackStack() {
        navController.popBackStack()
    }

    fun navigateToCreateThanksCard(invitationId: Long) {
        navController.navigateToCreateThanksCard(invitationId)
    }

    fun navigateToUpdateThanksCard(cardId: Long) {
        navController.navigateToUpdateThanksCard(cardId)
    }
}

@Composable
internal fun rememberInvitationNavigator(
    navController: NavHostController = rememberNavController(),
    startDestination: KClass<*> = Login::class
): NachoNavigator =
    remember(navController) {
        NachoNavigator(navController, startDestination)
    }
