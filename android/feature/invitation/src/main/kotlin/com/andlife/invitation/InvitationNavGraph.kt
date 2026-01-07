package com.andlife.invitation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.andlife.invitation.screen.InvitationDetailRoute
import com.andlife.invitation.screen.InvitationRoute
import kotlinx.serialization.Serializable

@Serializable
data object Invitation

@Serializable
data class InvitationDetail(
    val id: Long,
)

fun NavController.navigateToInvitation(navOptions: NavOptions) {
    navigate(Invitation, navOptions)
}

fun NavController.navigateToInvitationDetail(
    id: Long,
    navOptions: NavOptions,
) {
    navigate(InvitationDetail(id), navOptions)
}

fun NavGraphBuilder.invitationNavGraph(
    paddingValues: PaddingValues,
    onNavigateToDetail: (Long) -> Unit,
) {
    composable<Invitation> {
        InvitationRoute(
            onNavigateToDetail = onNavigateToDetail,
            modifier = Modifier.padding(paddingValues),
        )
    }
}

fun NavGraphBuilder.invitationDetailNavGraph(
    onNavigateBack: () -> Unit,
) {
    composable<InvitationDetail>(
        deepLinks = listOf(
            navDeepLink<InvitationDetail>(
                basePath = "https://invitationzzang.com/invite"
            ),
            // TODO: 로컬 테스트용 ngrok 도메인 삭제 예정
            navDeepLink<InvitationDetail>(
                basePath = "https://fenny-dell-unintrigued.ngrok-free.dev/invite"
            )
        )
    ) {
        InvitationDetailRoute(
            onNavigateBack = onNavigateBack,
            modifier = Modifier.padding()
        )
    }
}
