package com.andlife.home

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable data object Home

fun NavController.navigateToHome(navOptions: NavOptions) {
    navigate(Home, navOptions)
}


fun NavGraphBuilder.homeNavGraph() {
    composable<Home> {

    }
}