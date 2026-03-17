package com.andlife.invitation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.andlife.invitation.screen.detail.InvitationDetailRoute
import com.andlife.invitation.screen.InvitationsListDetailRoute
import com.andlife.invitation.viewmodel.InvitationDetailViewModel
import com.andlife.invitation.viewmodel.InvitationGuestBookViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable

@Serializable
data class Invitation(
    val initialInvitationId: Long? = null,
    val isFromDeepLink: Boolean = false
)

@Serializable
data class InvitationDetail(
    val id: Long,
    val isFromDeepLink: Boolean = false
)

@Serializable
internal data object InvitationPlaceholder

fun NavController.navigateToInvitation(
    navOptions: NavOptions? = null,
    initialInvitationId: Long? = null,
    isFromDeepLink: Boolean = false
) {
    navigate(Invitation(initialInvitationId, isFromDeepLink), navOptions)
}

fun NavController.navigateToInvitationDetail(
    id: Long,
    navOptions: NavOptions,
) {
    navigate(InvitationDetail(id), navOptions)
}

fun NavGraphBuilder.invitationNavGraph(
    deepLinks: NavDeepLink,
    onNavigateToLogin: () -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    composable<Invitation>(
        deepLinks = persistentListOf(deepLinks)
    ) {
        InvitationsListDetailRoute(
            snackbarHostState = snackbarHostState,
            onNavigateToLogin = onNavigateToLogin,
        )
    }
}

fun NavGraphBuilder.invitationDetailNavGraph(
    deepLinks: NavDeepLink,
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    composable<InvitationDetail>(
    ) {
        val invitationDetail = it.toRoute<InvitationDetail>()
        InvitationDetailRoute(
            selectedId = invitationDetail.id,
            onNavigateBack = onNavigateBack,
            onNavigateToLogin = onNavigateToLogin,
            modifier = Modifier.padding(),
            viewModel = hiltViewModel<InvitationDetailViewModel, InvitationDetailViewModel.Factory> { factory ->
                factory.create(invitationDetail.id, false)
            },
            guestBookViewModel = hiltViewModel<InvitationGuestBookViewModel, InvitationGuestBookViewModel.Factory> { factory ->
                factory.create(invitationDetail.id)
            }
        )
    }
}
