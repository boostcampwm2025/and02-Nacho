package com.andlife.invitation_card

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.andlife.invitation_card.screen.createbyinvitation.CreateCardByInvitationRoute
import com.andlife.invitation_card.screen.createcard.CreateCardRoute
import com.andlife.invitation_card.screen.updateacard.UpdateCardRoute
import kotlinx.serialization.Serializable

@Serializable
data object CreateCard

@Serializable
data class CreateCardByInvitation(val invitationId: Long)

@Serializable
data class UpdateCard(val cardId: Long)

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
    onSuccessCreateCard: () -> Unit,
) {
    composable<CreateCardByInvitation> { backStackEntry ->
        CreateCardByInvitationRoute(
            onBackClick = onBackClick,
            onSuccessCreateCard = onSuccessCreateCard
        )
    }
}

fun NavController.navigateToUpdateCard(cardId: Long) {
    navigate(UpdateCard(cardId))
}

fun NavGraphBuilder.updateCardNavGraph(
    onBackClick: () -> Unit,
    onSuccessCreateCard: () -> Unit,
) {
    composable<UpdateCard> { backStackEntry ->
        UpdateCardRoute(
            onSuccessfulUpdate = onSuccessCreateCard,
            onBackNavigation = onBackClick,
        )
    }
}
