package com.andlife.invitationzzang

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.andlife.invitationzzang.navigation.InvitationNavHost
import com.andlife.invitationzzang.navigation.rememberInvitationNavigator

@Composable
fun InvitationApp(modifier: Modifier = Modifier) {
    val navigator = rememberInvitationNavigator()
    InvitationNavHost(
        navigator = navigator,
        modifier = modifier,
    )
}
