package com.andlife.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.andlife.home.screen.HomeRoute
import com.andlife.home.screen.SettingRoute
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
    onNavigateToInvitationDetail: (Long) -> Unit,
    onNavigateToSetting: () -> Unit,
) {
    composable<Home> {
        HomeRoute(
            onNavigateToInvitationDetail = onNavigateToInvitationDetail,
            //onNavigateToSetting = onNavigateToSetting,
            modifier = Modifier.padding(paddingValues),
        )
    }
}

fun NavGraphBuilder.settingNavGraph(
    onNavigateBack: () -> Unit,
) {
    composable<Setting> {
        SettingRoute(
            onNavigateBack = onNavigateBack,
            modifier = Modifier.padding(),
        )
    }
}
