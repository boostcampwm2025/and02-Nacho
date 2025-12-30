package com.andlife.myinvitation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.andlife.myinvitation.mapper.toUiModel
import com.andlife.myinvitation.screen.AddressSearchRoute
import com.andlife.myinvitation.screen.MyInvitationCreateRoute
import com.andlife.myinvitation.screen.MyInvitationRoute
import com.andlife.ui.model.AddressUiModel
import kotlinx.serialization.Serializable

@Serializable
data object MyInvitation

@Serializable
data class MyInvitationCreate(val id: Long)

@Serializable
data object AddressSearch

fun NavController.navigateToMyInvitation(navOptions: NavOptions) {
    navigate(MyInvitation, navOptions)
}

fun NavController.navigateToMyInvitationCreate(id: Long, navOptions: NavOptions, ) {
    navigate(MyInvitationCreate(id), navOptions)
}

fun NavController.navigateToAddressSearch(navOptions: NavOptions) {
    navigate(AddressSearch, navOptions)
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

fun NavGraphBuilder.myInvitationCreateNavGraph(
    paddingValues: PaddingValues,
    onNavigateToAddressSearch: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    composable<MyInvitationCreate> { backStackEntry ->
        val selectedAddressUiModel = backStackEntry.savedStateHandle
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
    onBack: () -> Unit,
) {
    composable<AddressSearch> {
        AddressSearchRoute(
            onBack = onBack,
            onAddressSelected = { address ->
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("selected_address", address.toUiModel())
                onBack()
            },
            modifier = Modifier.padding(paddingValues),
        )
    }
}
