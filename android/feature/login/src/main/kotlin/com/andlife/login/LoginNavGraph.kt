package com.andlife.login

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.andlife.login.screem.LoginRoute
import kotlinx.serialization.Serializable

//@Serializable
//data object Login

@Serializable
data class Login(val fromSplash: Boolean = true)

fun NavController.navigateToLogin(fromSplash: Boolean) {
    navigate(Login(fromSplash))
}

fun NavGraphBuilder.loginNavGraph(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    composable<Login> {
        LoginRoute(onNavigateBack = onNavigateBack)
    }
}
