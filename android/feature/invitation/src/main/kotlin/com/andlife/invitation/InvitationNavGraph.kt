package com.andlife.invitation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.andlife.invitation.screen.InvitationRoute
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
    navOptions: NavOptions,
) {
    navigate(InvitationDetail(id), navOptions)
}

fun NavGraphBuilder.invitationNavGraph(
    paddingValues: PaddingValues,
    onNavigateToDetail: (Long) -> Unit,
) {
    composable<Invitation> {
        InvitationRoute(
            onNavigateToDetail = onNavigateToDetail,
            modifier = Modifier.padding(paddingValues),
        )
    }
}

fun NavGraphBuilder.invitationDetailNavGraph(
    deepLinks: NavDeepLink,
    onNavigateBack: () -> Unit,
) {
    composable<InvitationDetail>(
        deepLinks = listOf(deepLinks),
    ) {
        InvitationDetailRoute(
            onNavigateBack = onNavigateBack,
            modifier = Modifier.padding(),
        )
    }
}
