package com.andlife.invitation_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.andlife.invitation_edit.model.AddressUiModel
import com.andlife.invitation_edit.screen.address.AddressSearchRoute
import com.andlife.invitation_edit.screen.create.MyInvitationCreateRoute
import com.andlife.invitation_edit.screen.preview.InvitationPreviewRoute
import com.andlife.invitation_edit.viewmodel.CreateInvitationViewModel
import kotlinx.serialization.Serializable

@Serializable
data object MyInvitationCreate

@Serializable
data object AddressSearch

@Serializable
data object InvitationPreview

fun NavController.navigateToMyInvitationCreate(navOptions: NavOptions) {
    navigate(MyInvitationCreate, navOptions)
}

fun NavController.navigateToAddressSearch(navOptions: NavOptions) {
    navigate(AddressSearch, navOptions)
}

fun NavController.navigateToInvitationPreview(navOptions: NavOptions) {
    navigate(InvitationPreview, navOptions)
}

fun NavGraphBuilder.myInvitationCreateNavGraph(
    onNavigateToAddressSearch: () -> Unit,
    onNavigateToPreview: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateCreateCard: () -> Unit,
    onNavigateToInvitationDetail: (Long) -> Unit,
) {
    composable<MyInvitationCreate> { backStackEntry ->
        val selectedAddressUiModel by
        backStackEntry.savedStateHandle
            .getStateFlow<AddressUiModel?>("selected_address", null)
            .collectAsStateWithLifecycle()

        MyInvitationCreateRoute(
            onNavigateToAddressSearch = onNavigateToAddressSearch,
            onNavigateToPreview = onNavigateToPreview,
            onNavigateBack = onNavigateBack,
            modifier = Modifier,
            address = selectedAddressUiModel,
            onNavigateCreateCard = onNavigateCreateCard,
            onNavigateToInvitationDetail = onNavigateToInvitationDetail
        )
    }
}

fun NavGraphBuilder.invitationPreviewNavGraph(
    navController: NavController,
    onNavigateBack: () -> Unit,
) {
    composable<InvitationPreview> { backStackEntry ->
        val parentEntry = remember(backStackEntry) {
            navController.previousBackStackEntry ?: backStackEntry
        }

        val viewModel: CreateInvitationViewModel = hiltViewModel(parentEntry)

        InvitationPreviewRoute(
            onNavigateBack = onNavigateBack,
            viewModel = viewModel,
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
                    ?.set("selected_address", addressUiModel)
                onNavigateBack()
            },
        )
    }
}
