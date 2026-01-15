package com.andlife.invitation_card.createcard

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.andlife.invitation_card.editor.screen.EditorScreen
import kotlinx.serialization.Serializable

@Serializable
data object CreateCard

fun NavController.navigateToEditor(navOptions: NavOptions) {
    navigate(CreateCard, navOptions)
}

fun NavGraphBuilder.editorNavGraph(
    onNavigateBack: () -> Unit,
) {
    composable<CreateCard> {
        EditorScreen(
            titleText = "초대카드 생성",
            onBackClick = onNavigateBack,
            onSaveChangesClick = {},
        )
    }
}
