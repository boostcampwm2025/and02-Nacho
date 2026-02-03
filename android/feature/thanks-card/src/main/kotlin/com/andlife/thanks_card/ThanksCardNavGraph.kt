package com.andlife.thanks_card

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.andlife.thanks_card.screen.ThanksCardRoute
import com.andlife.thanks_card.screen.UpdateThanksCardRoute
import kotlinx.serialization.Serializable

@Serializable
data class CreateThanksCard(val invitationId: Long)

@Serializable
data class UpdateThanksCard(val cardId: Long)

fun NavController.navigateToCreateThanksCard(invitationId: Long) {
    navigate(CreateThanksCard(invitationId))
}

fun NavGraphBuilder.createThanksCardNavGraph(
    onSuccessfulCreate: () -> Unit,
    onBackClick: () -> Unit,
) {
    composable<CreateThanksCard> { backStackEntry ->
        ThanksCardRoute(
            onSuccess = onSuccessfulCreate,
            onBackClick = onBackClick
        )
    }
}

fun NavController.navigateToUpdateThanksCard(cardId: Long) {
    navigate(UpdateThanksCard(cardId))
}

fun NavGraphBuilder.updateThanksCardNavGraph(
   onSuccessfulUpdate: () -> Unit,
   onBackClick: () -> Unit,
) {
    composable<UpdateThanksCard> { backStackEntry ->
        UpdateThanksCardRoute(
            onSuccessfulUpdate = onSuccessfulUpdate,
            onBackClick = onBackClick
        )
    }
}
