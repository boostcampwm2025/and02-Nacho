package com.andlife.myinvitation

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
import com.andlife.model.util.NavigationKeyConstant.CREATE_CARD_BY_INVITATION_ID
import com.andlife.model.util.NavigationKeyConstant.INVITATION_UPDATED
import com.andlife.model.util.NavigationKeyConstant.UPDATE_CARD
import com.andlife.myinvitation.model.detail.MyInvitationDetailUiEvent
import com.andlife.myinvitation.screen.MyInvitationDetailRoute
import com.andlife.myinvitation.screen.MyInvitationRoute
import com.andlife.myinvitation.viewmodel.MyInvitationDetailViewModel
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
    paddingValues: PaddingValues,
    onNavigateToCreate: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    composable<MyInvitation> {
        MyInvitationRoute(
            snackbarHostState = snackbarHostState,
            onNavigateToCreate = onNavigateToCreate,
            onNavigateToDetail = onNavigateToDetail,
            onNavigateToLogin = onNavigateToLogin,
            modifier = Modifier.padding(paddingValues),
        )
    }
}

fun NavGraphBuilder.myInvitationDetailNavGraph(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToEditInvitation: (Long) -> Unit,
    onNavigateToEditCard: (Long) -> Unit,
    onNavigateToCreateCard: (Long) -> Unit,
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

        LaunchedEffect(cardCreated, cardUpdated, invitationUpdated) {
            if (cardCreated || cardUpdated || invitationUpdated) {
                viewModel.onEvent(MyInvitationDetailUiEvent.RetryLoad)
                backStackEntry.savedStateHandle.remove<Boolean>(CREATE_CARD_BY_INVITATION_ID)
                backStackEntry.savedStateHandle.remove<Boolean>(UPDATE_CARD)
                backStackEntry.savedStateHandle.remove<Boolean>(INVITATION_UPDATED)
            }
        }

        MyInvitationDetailRoute(
            onNavigateBack = onNavigateBack,
            onNavigateToLogin = onNavigateToLogin,
            onNavigateToEditInvitation = onNavigateToEditInvitation,
            onNavigateToEditCard = onNavigateToEditCard,
            onNavigateToCreateCard = onNavigateToCreateCard,
            modifier = Modifier.padding(),
            viewModel = viewModel
        )
    }
}
