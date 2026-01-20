package com.andlife.invitation_card

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.andlife.invitation_card.screen.createcard.CreateCardRoute
import kotlinx.serialization.Serializable

@Serializable
data object CreateCard

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
