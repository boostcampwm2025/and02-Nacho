package com.andlife.myinvitation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable data object MyInvitation

fun NavController.navigateToMyInvitation(navOptions: NavOptions) {
    navigate(MyInvitation, navOptions)
}


fun NavGraphBuilder.myInvitationNavGraph() {
    composable<MyInvitation> {

    }
}