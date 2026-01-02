package com.andlife.invitation_edit

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.andlife.invitation_edit.model.AddressUiModel
import com.andlife.invitation_edit.screen.AddressSearchRoute
import com.andlife.invitation_edit.screen.MyInvitationCreateRoute
import kotlinx.serialization.Serializable

@Serializable
data class MyInvitationCreate(
    val id: Long,
)

@Serializable
data object AddressSearch

fun NavController.navigateToMyInvitationCreate(
    id: Long,
    navOptions: NavOptions,
) {
    navigate(MyInvitationCreate(id), navOptions)
}

fun NavController.navigateToAddressSearch(navOptions: NavOptions) {
    navigate(AddressSearch, navOptions)
}

fun NavGraphBuilder.myInvitationCreateNavGraph(
    paddingValues: PaddingValues,
    onNavigateToAddressSearch: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    composable<MyInvitationCreate> { backStackEntry ->
        val selectedAddressUiModel =
            backStackEntry.savedStateHandle
                .getStateFlow<AddressUiModel?>("selected_address", null)
                .collectAsStateWithLifecycle()

        MyInvitationCreateRoute(
            selectedAddress = selectedAddressUiModel.value,
            onNavigateToAddressSearch = onNavigateToAddressSearch,
            onNavigateBack = onNavigateBack,
            modifier = Modifier.padding(paddingValues),
        )
    }
}

fun NavGraphBuilder.addressSearchNavGraph(
    navController: NavController,
    paddingValues: PaddingValues,
    onNavigateBack: () -> Unit,
) {
    composable<AddressSearch> {
        AddressSearchRoute(
            onNavigateBack = onNavigateBack,
            onAddressSelected = { addressUiModel ->
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("selected_address", addressUiModel)
                onNavigateBack()
            },
            modifier = Modifier.padding(paddingValues),
        )
    }
}
