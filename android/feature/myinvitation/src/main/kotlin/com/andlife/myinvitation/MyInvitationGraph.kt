package com.andlife.myinvitation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.andlife.model.util.NavigationKeyConstant.CREATE_CARD_BY_INVITATION_ID
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
    paddingValues: PaddingValues,
    onNavigateToCreate: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
) {
    composable<MyInvitation> {
        MyInvitationRoute(
            onNavigateToCreate = onNavigateToCreate,
            onNavigateToDetail = onNavigateToDetail,
            modifier = Modifier.padding(paddingValues),
        )
    }
}

fun NavGraphBuilder.myInvitationDetailNavGraph(
    onNavigateBack: () -> Unit,
    onNavigateToEditCard: (Long) -> Unit,
    onNavigateToCreateCard: (Long) -> Unit,
) {
    composable<MyInvitationDetail> { backStackEntry ->
        val viewModel: MyInvitationDetailViewModel = hiltViewModel()
        val cardCreated = backStackEntry.savedStateHandle.getStateFlow<Boolean>(
            CREATE_CARD_BY_INVITATION_ID, false
        ).collectAsStateWithLifecycle()

        LaunchedEffect(cardCreated.value) {
            if (cardCreated.value) {
                viewModel.onEvent(MyInvitationDetailUiEvent.RetryLoad)
                backStackEntry.savedStateHandle.remove<Boolean>(CREATE_CARD_BY_INVITATION_ID)
            }
        }

        MyInvitationDetailRoute(
            onNavigateBack = onNavigateBack,
            onNavigateToEditCard = onNavigateToEditCard,
            onNavigateToCreateCard = onNavigateToCreateCard,
            modifier = Modifier.padding(),
            viewModel = viewModel
        )
    }
}
