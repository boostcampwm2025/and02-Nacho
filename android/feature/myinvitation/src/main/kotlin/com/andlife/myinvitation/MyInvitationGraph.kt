package com.andlife.myinvitation

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
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.andlife.domain.util.RefreshEventHub
import com.andlife.model.util.NavigationKeyConstant.CREATE_CARD_BY_INVITATION_ID
import com.andlife.model.util.NavigationKeyConstant.CREATE_THANKS_CARD
import com.andlife.model.util.NavigationKeyConstant.INVITATION_UPDATED
import com.andlife.model.util.NavigationKeyConstant.UPDATE_CARD
import com.andlife.myinvitation.model.detail.MyInvitationDetailUiEvent
import com.andlife.myinvitation.screen.MyInvitationDetailRoute
import com.andlife.myinvitation.screen.MyInvitationRoute
import com.andlife.myinvitation.viewmodel.MyInvitationDetailViewModel
import com.andlife.myinvitation.viewmodel.MyInvitationViewModel
import kotlinx.serialization.Serializable

@Serializable
data object MyInvitation

@Serializable
data class MyInvitationDetail(
    val id: Long,
)

fun NavController.navigateToMyInvitation(navOptions: NavOptions) {
    navigate(MyInvitation, navOptions)
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
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToLogin: () -> Unit,
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

        MyInvitationRoute(
            viewModel = viewModel,
            snackbarHostState = snackbarHostState,
            onNavigateToCreate = onNavigateToCreate,
            onNavigateToDetail = onNavigateToDetail,
            onNavigateToLogin = onNavigateToLogin,
            modifier = Modifier
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
    composable<MyInvitationDetail> { backStackEntry ->
        val viewModel: MyInvitationDetailViewModel = hiltViewModel()
        val cardCreated by backStackEntry.savedStateHandle.getStateFlow<Boolean>(
            CREATE_CARD_BY_INVITATION_ID, false
        ).collectAsStateWithLifecycle()

        val cardUpdated by backStackEntry.savedStateHandle.getStateFlow<Boolean>(
            UPDATE_CARD, false
        ).collectAsStateWithLifecycle()

        val invitationUpdated by backStackEntry.savedStateHandle.getStateFlow<Boolean>(
            INVITATION_UPDATED, false
        ).collectAsStateWithLifecycle()

        val thanksCardCreated by backStackEntry.savedStateHandle.getStateFlow<Boolean>(
            CREATE_THANKS_CARD, false
        ).collectAsStateWithLifecycle()

        val thanksCardUpdated by backStackEntry.savedStateHandle.getStateFlow<Boolean>(
            UPDATE_CARD, false
        ).collectAsStateWithLifecycle()


        LaunchedEffect(cardCreated, cardUpdated, invitationUpdated, thanksCardCreated, thanksCardUpdated) {
            if (cardCreated || cardUpdated || invitationUpdated || thanksCardCreated || thanksCardUpdated) {
                viewModel.onEvent(MyInvitationDetailUiEvent.RetryLoad)
                backStackEntry.savedStateHandle.remove<Boolean>(CREATE_CARD_BY_INVITATION_ID)
                backStackEntry.savedStateHandle.remove<Boolean>(UPDATE_CARD)
                backStackEntry.savedStateHandle.remove<Boolean>(INVITATION_UPDATED)
                backStackEntry.savedStateHandle.remove<Boolean>(CREATE_THANKS_CARD)
            }
        }

        MyInvitationDetailRoute(
            onNavigateBack = onNavigateBack,
            onNavigateToLogin = onNavigateToLogin,
            onNavigateToEditInvitation = onNavigateToEditInvitation,
            onNavigateToEditCard = onNavigateToEditCard,
            onNavigateToCreateCard = onNavigateToCreateCard,
            onNavigateToCreateThanksCard = onNavigateToCreateThanksCard,
            modifier = Modifier.padding(),
            onNavigateToUpdateThanksCard = onNavigateToUpdateThanksCard,
            viewModel = viewModel
        )
    }
}
