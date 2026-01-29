package com.andlife.invitation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.andlife.invitation.screen.InvitationRoute
import com.andlife.invitation.screen.detail.InvitationDetailRoute
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable

@Serializable
data object Invitation

@Serializable
data class InvitationDetail(
    val id: Long,
    val isFromDeepLink: Boolean = false
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
    snackbarHostState: SnackbarHostState,
) {
    composable<Invitation> {
        InvitationRoute(
            onNavigateToDetail = onNavigateToDetail,
            modifier = Modifier.padding(paddingValues),
            snackbarHostState = snackbarHostState
        )
    }
}

fun NavGraphBuilder.invitationDetailNavGraph(
    deepLinks: NavDeepLink,
    onNavigateBack: () -> Unit,
    onNavigateToLogin: (Boolean) -> Unit,
) {
    composable<InvitationDetail>(
        deepLinks = persistentListOf(deepLinks),
    ) {
        InvitationDetailRoute(
            onNavigateBack = onNavigateBack,
            onNavigateToLogin = onNavigateToLogin,
            modifier = Modifier.padding(),
        )
    }
}
