package com.andlife.setting

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.andlife.setting.screen.SettingRoute
import com.andlife.setting.screen.WebViewScreen
import kotlinx.serialization.Serializable

object PolicyUrl {
    const val SERVICE = "https://adventurous-pyramid-33a.notion.site/305507e6a51280b5b325e368dff9b81c"
    const val PRIVACY = "https://adventurous-pyramid-33a.notion.site/305507e6a512804da1aacbb2180d3be9"
}

@Serializable
data object Setting

@Serializable
data class WebViewRoute(val url: String, val title: String)

fun NavController.navigateToSetting(navOptions: NavOptions) {
    navigate(Setting, navOptions)
}

fun NavController.navigateToWebView(url: String, title: String, navOptions: NavOptions) {
    navigate(WebViewRoute(url, title), navOptions)
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

fun NavGraphBuilder.webViewNavGraph(
    onNavigateBack: () -> Unit,
) {
    composable<WebViewRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<WebViewRoute>()
        WebViewScreen(
            url = route.url,
            title = route.title,
            onNavigateBack = onNavigateBack,
        )
    }
}
