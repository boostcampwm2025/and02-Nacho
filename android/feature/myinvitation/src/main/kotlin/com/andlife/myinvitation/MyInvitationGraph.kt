package com.andlife.myinvitation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.andlife.myinvitation.screen.MyInvitationDetailRoute
import com.andlife.myinvitation.screen.MyInvitationRoute
import kotlinx.serialization.Serializable

@Serializable
data object MyInvitation

@Serializable
data class MyInvitationDetail(
    val id: Long,
)

fun NavController.navigateToMyInvitation(navOptions: NavOptions) {
    navigate(MyInvitation, navOptions)
}

fun NavController.navigateToMyInvitationDetail(
    id: Long,
    navOptions: NavOptions,
) {
    navigate(MyInvitationDetail(id), navOptions)
}

fun NavGraphBuilder.myInvitationNavGraph(
    paddingValues: PaddingValues,
    onNavigateToCreate: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
) {
    composable<MyInvitation> {
        MyInvitationRoute(
            onNavigateToCreate = onNavigateToCreate,
            onNavigateToDetail = onNavigateToDetail,
            modifier = Modifier.padding(paddingValues),
        )
    }
}

fun NavGraphBuilder.myInvitationDetailNavGraph(
    onNavigateBack: () -> Unit,
) {
    composable<MyInvitationDetail> {
        MyInvitationDetailRoute(
            onNavigateBack = onNavigateBack,
            modifier = Modifier.padding(),
        )
    }
}
