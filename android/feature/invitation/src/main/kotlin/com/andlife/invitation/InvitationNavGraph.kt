package com.andlife.invitation

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.andlife.invitation.screen.InvitationRoute
import com.andlife.invitation.screen.detail.InvitationDetailRoute
import com.andlife.invitation.viewmodel.InvitationViewModel
import com.andlife.domain.util.RefreshEventHub
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
        val viewModel: InvitationViewModel = hiltViewModel()

        val needsRefresh by RefreshEventHub.invitationRefresh.collectAsStateWithLifecycle()

        LaunchedEffect(needsRefresh) {
            Log.d("RefreshEventHub", "invitation needsRefresh: $needsRefresh")
            if (needsRefresh) {
                viewModel.handleRefresh()
                RefreshEventHub.consumeInvitation()
            }
        }

        InvitationRoute(
            viewModel = viewModel,
            onNavigateToDetail = onNavigateToDetail,
            snackbarHostState = snackbarHostState,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

fun NavGraphBuilder.invitationDetailNavGraph(
    deepLinks: NavDeepLink,
    onNavigateBack: () -> Unit,
) {
    composable<InvitationDetail>(
        deepLinks = persistentListOf(deepLinks),
    ) {
        InvitationDetailRoute(
            onNavigateBack = onNavigateBack,
            modifier = Modifier.padding(),
        )
    }
}
