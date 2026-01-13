package com.andlife.nacho

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.andlife.invitation.InvitationDetail
import com.andlife.nacho.navigation.NachoNavHost
import com.andlife.nacho.navigation.rememberInvitationNavigator

@Composable
fun NachoApp(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel(),
) {
    val navigator = rememberInvitationNavigator()
    val deepLinkManager = viewModel.deepLinkManager


    LaunchedEffect(Unit) {
        viewModel.deepLinkEvent.collect { intent ->
            Log.d("NachoApp", "Received deepLink intent: ${intent.data}")
            navigator.navController.handleDeepLink(intent)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.navigateToDetail.collect { invitationId ->
            Log.d("NachoApp", "Received Deferred DeepLink invitationId: $invitationId")
            navigator.navController.navigate(InvitationDetail(invitationId)) {
                popUpTo(navigator.navController.graph.startDestinationId)
                launchSingleTop = true
            }
        }
    }

    NachoNavHost(
        navigator = navigator,
        deepLinkManager = deepLinkManager,
        modifier = modifier,
    )
}
