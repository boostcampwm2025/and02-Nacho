package com.andlife.invitation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.andlife.invitation.screen.InvitationScreen
import kotlinx.serialization.Serializable

@Serializable
data object Invitation

fun NavController.navigateToInvitation(navOptions: NavOptions) {
    navigate(Invitation, navOptions)
}

fun NavGraphBuilder.invitationNavGraph(
    paddingValues: PaddingValues
) {
    composable<Invitation> {
        InvitationScreen(modifier = Modifier.padding(paddingValues))
    }
}