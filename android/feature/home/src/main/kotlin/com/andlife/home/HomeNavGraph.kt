package com.andlife.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.andlife.home.screen.HomeRoute
import kotlinx.serialization.Serializable

@Serializable
data object Home

fun NavController.navigateToHome(navOptions: NavOptions) {
    navigate(Home, navOptions)
}

fun NavGraphBuilder.homeNavGraph(
    paddingValues: PaddingValues,
    onNavigateToInvitationDetail: (Long) -> Unit,
) {
    composable<Home> {
        HomeRoute(
            onNavigateToInvitationDetail = onNavigateToInvitationDetail,
            modifier = Modifier.padding(paddingValues),
        )
    }
}
