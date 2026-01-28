package com.andlife.nacho

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.andlife.invitation.InvitationDetail
import com.andlife.nacho.model.MainSideEffect
import com.andlife.nacho.navigation.NachoNavHost
import com.andlife.nacho.navigation.rememberInvitationNavigator
import com.andlife.nacho.viewmodel.MainViewModel
import com.andlife.ui.util.collectWithLifecycle

@Composable
fun NachoApp(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel(),
) {
    val navigator = rememberInvitationNavigator()
    val deepLinkManager = viewModel.deepLinkManager

    viewModel.effectFlow.collectWithLifecycle { effect ->
        when (effect) {
            is MainSideEffect.HandleDeepLink -> {
                Log.d("NachoApp", "Received deepLink intent: ${effect.intent.data}")
                navigator.navController.handleDeepLink(effect.intent)
            }

            is MainSideEffect.NavigateToDetail -> {
                Log.d("NachoApp", "Received Deferred DeepLink invitationId: ${effect.invitationId}")
                navigator.navController.navigate(
                    InvitationDetail(
                        id = effect.invitationId,
                        isFromDeepLink = true
                    )
                ) {
                    popUpTo(navigator.navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
        }
    }

    NachoNavHost(
        navigator = navigator,
        deepLinkManager = deepLinkManager,
        modifier = modifier,
    )
}
