package com.andlife.myinvitation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.andlife.myinvitation.screen.MyInvitationRoute
import kotlinx.serialization.Serializable

@Serializable
data object MyInvitation

fun NavController.navigateToMyInvitation(navOptions: NavOptions) {
    navigate(MyInvitation, navOptions)
}

fun NavGraphBuilder.myInvitationNavGraph(
    paddingValues: PaddingValues,
    onNavigateToCreate: () -> Unit,
) {
    composable<MyInvitation> {
        MyInvitationRoute(
            onNavigateToCreate = onNavigateToCreate,
            modifier = Modifier.padding(paddingValues),
        )
    }
}
