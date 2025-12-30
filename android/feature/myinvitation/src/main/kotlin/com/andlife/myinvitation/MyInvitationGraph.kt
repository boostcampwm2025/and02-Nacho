package com.andlife.myinvitation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.andlife.myinvitation.model.AddressUiModel
import com.andlife.myinvitation.model.toDomain
import com.andlife.myinvitation.model.toUiModel
import com.andlife.myinvitation.screen.AddressSearchRoute
import com.andlife.myinvitation.screen.MyInvitationDetailRoute
import com.andlife.myinvitation.screen.MyInvitationRoute
import kotlinx.serialization.Serializable

@Serializable
data object MyInvitation

@Serializable
data class MyInvitationDetail(val id: Long)

@Serializable
data object AddressSearch

fun NavController.navigateToMyInvitation(navOptions: NavOptions) {
    navigate(MyInvitation, navOptions)
}

fun NavController.navigateToMyInvitationDetail(id: Long, navOptions: NavOptions) {
    navigate(MyInvitationDetail(id), navOptions)
}

fun NavController.navigateToAddressSearch(navOptions: NavOptions) {
    navigate(AddressSearch, navOptions)
}

fun NavGraphBuilder.myInvitationNavGraph(
    paddingValues: PaddingValues,
    onNavigateToDetail: () -> Unit,
) {
    composable<MyInvitation> {
        MyInvitationRoute(
            onNavigateToDetail = onNavigateToDetail,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

fun NavGraphBuilder.myInvitationDetailNavGraph(
    paddingValues: PaddingValues,
    onNavigateToAddressSearch: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    composable<MyInvitationDetail> { backStackEntry ->
        val selectedAddressUiModel = backStackEntry.savedStateHandle
            .getStateFlow<AddressUiModel?>("selected_address", null)
            .collectAsStateWithLifecycle()

        MyInvitationDetailRoute(
            selectedAddress = selectedAddressUiModel.value?.toDomain(),
            onNavigateToAddressSearch = onNavigateToAddressSearch,
            onNavigateBack = onNavigateBack,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

fun NavGraphBuilder.addressSearchNavGraph(
    navController: NavController,
    paddingValues: PaddingValues,
    onClose: () -> Unit,
) {
    composable<AddressSearch> {
        AddressSearchRoute(
            onClose = onClose,
            onAddressSelected = { address ->
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("selected_address", address.toUiModel())
                onClose()
            },
            modifier = Modifier.padding(paddingValues),
        )
    }
}
