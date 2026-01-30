package com.andlife.invitation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.andlife.invitation.screen.InvitationRoute
import com.andlife.invitation.screen.detail.InvitationDetailRoute
import com.andlife.invitation.viewmodel.InvitationDetailViewModel
import com.andlife.invitation.viewmodel.InvitationViewModel
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
    composable<Invitation> { backStackEntry ->
        val viewModel: InvitationViewModel = hiltViewModel()

        val savedStateHandle = backStackEntry.savedStateHandle
        val shouldRefresh = savedStateHandle.get<Boolean>(InvitationDetailViewModel.KEY_SHOULD_REFRESH) ?: false

        LaunchedEffect(shouldRefresh) {
            if (shouldRefresh) {
                viewModel.handleDeepLinkRefresh()
                savedStateHandle[InvitationDetailViewModel.KEY_SHOULD_REFRESH] = false
            }
        }

        InvitationRoute(
            onNavigateToDetail = onNavigateToDetail,
            modifier = Modifier.padding(paddingValues),
            snackbarHostState = snackbarHostState,
            viewModel = viewModel
        )
    }
}

fun NavGraphBuilder.invitationDetailNavGraph(
    deepLinks: NavDeepLink,
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
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
