package com.andlife.invitation_card

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.andlife.invitation_card.screen.createbyinvitation.CreateCardByInvitationRoute
import com.andlife.invitation_card.screen.createcard.CreateCardRoute
import com.andlife.model.util.NavigationKeyConstant.CREATE_CARD_BY_INVITATION_ID
import kotlinx.serialization.Serializable

@Serializable
data object CreateCard

@Serializable
data class CreateCardByInvitation(val invitationId: Long)

fun NavController.navigateToCardEditor() {
    navigate(CreateCard)
}

fun NavGraphBuilder.createCardNavGraph(
    onBackClick: () -> Unit,
) {
    composable<CreateCard> {
        CreateCardRoute(
            onBackClick = onBackClick
        )
    }
}

fun NavController.navigateToCreateCardByInvitation(invitationId: Long) {
    navigate(CreateCardByInvitation(invitationId))
}

fun NavGraphBuilder.createCardByInvitationNavGraph(
    onBackClick: () -> Unit,
) {
    composable<CreateCardByInvitation> { backStackEntry ->
        CreateCardByInvitationRoute(
            onBackClick = onBackClick,
            onSuccessCreateCard = {
                backStackEntry.savedStateHandle[CREATE_CARD_BY_INVITATION_ID] = true
                onBackClick()
            }
        )
    }
}

