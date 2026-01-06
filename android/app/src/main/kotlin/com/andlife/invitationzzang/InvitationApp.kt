package com.andlife.invitationzzang

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.andlife.invitationzzang.navigation.InvitationNavHost
import com.andlife.invitationzzang.navigation.rememberInvitationNavigator

@Composable
fun InvitationApp(
    onNavControllerCreated: (NavHostController) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val navigator = rememberInvitationNavigator()
    LaunchedEffect(navigator.navController) {
        onNavControllerCreated(navigator.navController)
    }
    InvitationNavHost(
        navigator = navigator,
        modifier = modifier,
    )
}
