package com.andlife.nacho

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.andlife.nacho.navigation.InvitationNavHost
import com.andlife.nacho.navigation.rememberInvitationNavigator

@Composable
fun NachoApp(modifier: Modifier = Modifier) {
    val navigator = rememberInvitationNavigator()
    InvitationNavHost(
        navigator = navigator,
        modifier = modifier,
    )
}
