package com.andlife.invitation_edit

import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.andlife.invitation_edit.model.address.AddressUiModel
import com.andlife.invitation_edit.screen.address.AddressSearchRoute
import com.andlife.invitation_edit.screen.create.InvitationCreateRoute
import com.andlife.invitation_edit.screen.edit.InvitationEditRoute
import com.andlife.model.util.NavigationKeyConstant
import kotlinx.serialization.Serializable

@Serializable
data object InvitationCreate

@Serializable
data class InvitationEdit(
    val id: Long,
)

@Serializable
data object AddressSearch

fun NavController.navigateToInvitationCreate(navOptions: NavOptions) {
    navigate(InvitationCreate, navOptions)
}

fun NavController.navigateToInvitationEdit(
    id: Long,
    navOptions: NavOptions,
) {
    navigate(InvitationEdit(id), navOptions)
}

fun NavController.navigateToAddressSearch(navOptions: NavOptions) {
    navigate(AddressSearch, navOptions)
}

fun NavGraphBuilder.invitationCreateNavGraph(
    onNavigateToAddressSearch: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateCreateCard: () -> Unit,
    onNavigateToInvitationDetail: (Long) -> Unit,
) {
    composable<InvitationCreate> { backStackEntry ->
        val selectedAddressUiModel by
        backStackEntry.savedStateHandle
            .getStateFlow<AddressUiModel?>(NavigationKeyConstant.SELECTED_ADDRESS, null)
            .collectAsStateWithLifecycle()

        InvitationCreateRoute(
            onNavigateToAddressSearch = onNavigateToAddressSearch,
            onNavigateBack = onNavigateBack,
            modifier = Modifier,
            address = selectedAddressUiModel,
            onNavigateCreateCard = onNavigateCreateCard,
            onNavigateToInvitationDetail = onNavigateToInvitationDetail
        )
    }
}

fun NavGraphBuilder.invitationEditNavGraph(
    navController: NavController,
    onNavigateToAddressSearch: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    composable<InvitationEdit> { backStackEntry ->
        val selectedAddressUiModel by
        backStackEntry.savedStateHandle
            .getStateFlow<AddressUiModel?>(NavigationKeyConstant.SELECTED_ADDRESS, null)
            .collectAsStateWithLifecycle()

        InvitationEditRoute(
            onNavigateToAddressSearch = onNavigateToAddressSearch,
            onNavigateBack = onNavigateBack,
            modifier = Modifier,
            address = selectedAddressUiModel,
            onSuccessSave = {
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set(NavigationKeyConstant.INVITATION_UPDATED, true)
                onNavigateBack()
            },
        )
    }
}

fun NavGraphBuilder.addressSearchNavGraph(
    navController: NavController,
    onNavigateBack: () -> Unit,
) {
    composable<AddressSearch> {
        AddressSearchRoute(
            onNavigateBack = onNavigateBack,
            onAddressSelect = { addressUiModel ->
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set(NavigationKeyConstant.SELECTED_ADDRESS, addressUiModel)
                onNavigateBack()
            },
        )
    }
}
