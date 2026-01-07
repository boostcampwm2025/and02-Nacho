package com.andlife.invitation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.andlife.invitation.screen.InvitationScreen
import com.andlife.invitation.screen.detail.InvitationDetailRoute
import kotlinx.serialization.Serializable

@Serializable
data object Invitation

@Serializable
data class InvitationDetail(
    val id: Long,
)

fun NavController.navigateToInvitation(navOptions: NavOptions) {
    navigate(Invitation, navOptions)
}

fun NavController.navigateToInvitationDetail(
    id: Long,
    navOptions: NavOptions
) {
    navigate(InvitationDetail(id), navOptions)
}

fun NavGraphBuilder.invitationNavGraph(
    paddingValues: PaddingValues,
    onInvitationClick: (Long) -> Unit,
    onNavigationBack: () -> Unit,
) {
    composable<Invitation> {
        InvitationScreen(
            modifier = Modifier.padding(paddingValues),
            onInvitationClick = onInvitationClick,
        )
    }

    composable<InvitationDetail> { backStackEntry ->
        val arguments = backStackEntry.toRoute<InvitationDetail>()

        InvitationDetailRoute(
            id = arguments.id,
            onNavigateBack = onNavigationBack,
            modifier = Modifier.padding(paddingValues),
        )
    }
}
