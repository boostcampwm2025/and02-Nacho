package com.andlife.invitationzzang

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.andlife.invitationzzang.navigation.InvitationNavHost
import com.andlife.invitationzzang.navigation.rememberInvitationNavigator

@Composable
fun InvitationApp(
    modifier: Modifier = Modifier,
    viewModel: MainActivityViewModel = hiltViewModel(),
) {
    val navigator = rememberInvitationNavigator()
    val deepLinkManager = viewModel.deepLinkManager

    LaunchedEffect(Unit) {
        viewModel.deepLinkEvent.collect { intent ->
            Log.d("InvitationApp", "Received deepLink intent: ${intent.data}")
            navigator.navController.handleDeepLink(intent)
        }
    }

    InvitationNavHost(
        navigator = navigator,
        deepLinkManager = deepLinkManager,
        modifier = modifier,
    )
}
