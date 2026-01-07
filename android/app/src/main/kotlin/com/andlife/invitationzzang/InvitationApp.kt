package com.andlife.invitationzzang

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.andlife.invitationzzang.navigation.InvitationNavHost
import com.andlife.invitationzzang.navigation.rememberInvitationNavigator

@Composable
fun InvitationApp(
    modifier: Modifier = Modifier,
    viewModel: MainActivityViewModel = viewModel(),
) {
    val navigator = rememberInvitationNavigator()

    LaunchedEffect(Unit) {
        viewModel.deepLinkIntent.collect { intent ->
            Log.d("InvitationApp", "Received deepLink intent: ${intent.data}")
            navigator.navController.handleDeepLink(intent)
        }
    }

    InvitationNavHost(
        navigator = navigator,
        modifier = modifier,
    )
}
