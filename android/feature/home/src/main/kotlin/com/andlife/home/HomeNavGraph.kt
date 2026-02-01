package com.andlife.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.andlife.home.screen.HomeRoute
import com.andlife.home.screen.SettingRoute
import com.andlife.home.viewmodel.HomeViewModel
import com.andlife.model.util.NavigationEventhub
import com.andlife.model.util.NavigationEventhub.RefreshTarget
import kotlinx.serialization.Serializable

@Serializable
data object Home

@Serializable
data object Setting

fun NavController.navigateToHome(navOptions: NavOptions) {
    navigate(Home, navOptions)
}

fun NavController.navigateToSetting(navOptions: NavOptions) {
    navigate(Setting, navOptions)
}


fun NavGraphBuilder.homeNavGraph(
    paddingValues: PaddingValues,
    snackbarHostState: SnackbarHostState,
    onNavigateToCreate: () -> Unit,
    onNavigateToInvitationDetail: (Long) -> Unit,
    onNavigateToMyInvitationDetail: (Long) -> Unit,
    onNavigateToSetting: () -> Unit,
) {
    composable<Home> {
        val viewModel: HomeViewModel = hiltViewModel()

        val refreshTarget by NavigationEventhub.refreshEvent.collectAsStateWithLifecycle(initialValue = null)

        LaunchedEffect(refreshTarget) {
            if (refreshTarget == RefreshTarget.HOME || refreshTarget == RefreshTarget.ALL) {
                viewModel.handleRefresh()
            }
        }

        HomeRoute(
            snackbarHostState = snackbarHostState,
            onNavigateToCreate = onNavigateToCreate,
            onNavigateToInvitationDetail = onNavigateToInvitationDetail,
            onNavigateToMyInvitationDetail = onNavigateToMyInvitationDetail,
            onNavigateToSetting = onNavigateToSetting,
            modifier = Modifier.padding(paddingValues),
        )
    }
}

fun NavGraphBuilder.settingNavGraph(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    composable<Setting> {
        SettingRoute(
            onNavigateBack = onNavigateBack,
            modifier = Modifier.padding(),
            onNavigateToLogin = onNavigateToLogin
        )
    }
}
