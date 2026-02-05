package com.andlife.nacho

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.navOptions
import com.andlife.invitation.InvitationDetail
import com.andlife.login.Login
import com.andlife.nacho.model.MainSideEffect
import com.andlife.nacho.navigation.NachoNavHost
import com.andlife.nacho.navigation.rememberInvitationNavigator
import com.andlife.nacho.viewmodel.MainViewModel
import com.andlife.ui.util.collectWithLifecycle
import kotlin.reflect.KClass

@Composable
fun NachoApp(
    startDestination: KClass<*>,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel(),
) {
    val navigator = rememberInvitationNavigator(startDestination = startDestination)
    val deepLinkManager = viewModel.deepLinkManager

    viewModel.effectFlow.collectWithLifecycle { effect ->
        when (effect) {
            is MainSideEffect.NavigateToDetail -> {
                Log.d("DeepLink Debug", "Received Deferred DeepLink invitationId: ${effect.invitationId}")
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

            MainSideEffect.NavigateToHome -> {
                val navOptions = navOptions {
                    popUpTo(navigator.navController.graph.id) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
                navigator.navigateToHome(navOptions)
            }

            MainSideEffect.NavigateToLogin -> {
                val navOptions = navOptions {
                    popUpTo(navigator.navController.graph.id) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
                navigator.navigateToLogin(navOptions)
            }
        }
    }

    NachoNavHost(
        navigator = navigator,
        deepLinkManager = deepLinkManager,
        modifier = modifier,
    )
}
