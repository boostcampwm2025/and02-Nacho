package com.andlife.setting

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.andlife.setting.screen.SettingRoute
import kotlinx.serialization.Serializable

@Serializable
data object Setting

fun NavController.navigateToSetting(navOptions: NavOptions) {
    navigate(Setting, navOptions)
}

fun NavGraphBuilder.settingNavGraph(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToWebView: (String, String) -> Unit,
    onSignedOut: () -> Unit,
    onLogout: () -> Unit,
) {
    composable<Setting> {
        SettingRoute(
            onNavigateBack = onNavigateBack,
            modifier = Modifier.padding(),
            onNavigateToLogin = onNavigateToLogin,
            onNavigateToWebView = onNavigateToWebView,
            onSignedOut = onSignedOut,
            onLogout = onLogout
        )
    }
}
