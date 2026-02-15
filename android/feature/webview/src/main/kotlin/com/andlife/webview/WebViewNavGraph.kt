package com.andlife.webview

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.andlife.webview.screen.WebViewScreen
import kotlinx.serialization.Serializable

@Serializable
data class WebViewRoute(val url: String, val title: String)

fun NavController.navigateToWebView(url: String, title: String, navOptions: NavOptions) {
    navigate(WebViewRoute(url, title), navOptions)
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
