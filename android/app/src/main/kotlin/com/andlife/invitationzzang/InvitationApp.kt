package com.andlife.invitationzzang

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.andlife.invitationzzang.navigation.InvitationNavHost
import com.andlife.invitationzzang.navigation.rememberInvitationNavigator

@Composable
fun InvitationApp(
    deepLinkIntent: Intent? = null,
    modifier: Modifier = Modifier
) {
    val navigator = rememberInvitationNavigator()

    LaunchedEffect(deepLinkIntent) {
        deepLinkIntent?.let { intent ->
            if (intent.data != null) {
                navigator.navController.handleDeepLink(intent)
            }
        }
    }

    InvitationNavHost(
        navigator = navigator,
        modifier = modifier,
    )
}
