package com.andlife.login

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.andlife.login.screem.LoginRoute
import kotlinx.serialization.Serializable

@Serializable
data object Login

fun NavController.navigateToLogin(navOptions: NavOptions) {
    navigate(Login, navOptions)
}

fun NavGraphBuilder.loginNavGraph(
    modifier: Modifier = Modifier
) {
    composable<Login> {
        LoginRoute()
    }
}
