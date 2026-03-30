package com.andlife.myinvitation

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.andlife.domain.util.RefreshEventHub
import com.andlife.myinvitation.screen.MyInvitationListDetailRoute
import com.andlife.myinvitation.screen.detail.MyInvitationDetailRoute
import com.andlife.myinvitation.viewmodel.MyInvitationDetailViewModel
import com.andlife.myinvitation.viewmodel.MyInvitationGuestBookViewModel
import com.andlife.myinvitation.viewmodel.MyInvitationViewModel
import kotlinx.serialization.Serializable

@Serializable
data class MyInvitation(
    val initialInvitationId: Long? = null,
)

@Serializable
data class MyInvitationDetail(
    val id: Long,
)

internal data object MyInvitationPlaceholder


fun NavController.navigateToMyInvitation(navOptions: NavOptions, id: Long? = null) {
    navigate(MyInvitation(id), navOptions)
}

fun NavController.navigateToMyInvitationDetail(
    id: Long,
    navOptions: NavOptions,
) {
    navigate(MyInvitationDetail(id), navOptions)
}

fun NavGraphBuilder.myInvitationNavGraph(
    snackbarHostState: SnackbarHostState,
    onNavigateToCreate: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToEditInvitation: (Long) -> Unit,
    onNavigateToEditCard: (Long) -> Unit,
    onNavigateToCreateCard: (Long) -> Unit,
    onNavigateToCreateThanksCard: (Long) -> Unit,
    onNavigateToUpdateThanksCard: (Long) -> Unit,
) {
    composable<MyInvitation> {
        val viewModel: MyInvitationViewModel = hiltViewModel()

        val needsRefresh by RefreshEventHub.myInvitationRefresh.collectAsStateWithLifecycle()

        LaunchedEffect(needsRefresh) {
            Log.d("RefreshEventHub", "myInvitation needsRefresh: $needsRefresh")
            if (needsRefresh) {
                viewModel.handleRefresh()
                RefreshEventHub.consumeMyInvitation()
            }
        }

        MyInvitationListDetailRoute(
            snackbarHostState = snackbarHostState,
            onNavigateToCreate = onNavigateToCreate,
            onNavigateToLogin = onNavigateToLogin,
            onNavigateToEditInvitation = onNavigateToEditInvitation,
            onNavigateToEditCard = onNavigateToEditCard,
            onNavigateToCreateCard = onNavigateToCreateCard,
            onNavigateToCreateThanksCard = onNavigateToCreateThanksCard,
            onNavigateToUpdateThanksCard = onNavigateToUpdateThanksCard,
        )
    }
}

fun NavGraphBuilder.myInvitationDetailNavGraph(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToEditInvitation: (Long) -> Unit,
    onNavigateToEditCard: (Long) -> Unit,
    onNavigateToCreateCard: (Long) -> Unit,
    onNavigateToCreateThanksCard: (Long) -> Unit,
    onNavigateToUpdateThanksCard: (Long) -> Unit,
) {
    composable<MyInvitationDetail> { backStackEntry  ->

        val id = backStackEntry.toRoute<MyInvitationDetail>().id

        MyInvitationDetailRoute(
            selectedId = id,
            onNavigateBack = onNavigateBack,
            onNavigateToLogin = onNavigateToLogin,
            onNavigateToEditInvitation = onNavigateToEditInvitation,
            onNavigateToEditCard = onNavigateToEditCard,
            onNavigateToCreateCard = onNavigateToCreateCard,
            onNavigateToCreateThanksCard = onNavigateToCreateThanksCard,
            modifier = Modifier.padding(),
            onNavigateToUpdateThanksCard = onNavigateToUpdateThanksCard,
            viewModel = hiltViewModel<MyInvitationDetailViewModel, MyInvitationDetailViewModel.Factory>(
                key = "myDetail $id"
            ) { factory ->
                factory.create(id)
            },
            guestBookViewModel = hiltViewModel<MyInvitationGuestBookViewModel, MyInvitationGuestBookViewModel.Factory>(
                key = "myguestBook $id"
            ) { factory ->
                factory.create(id)
            },
        )
    }
}
