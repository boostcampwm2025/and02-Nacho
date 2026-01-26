package com.andlife.invitation_edit.screen.edit

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.andlife.invitation_edit.model.AddressUiModel

@Composable
fun MyInvitationEditRoute(
    onNavigateToAddressSearch: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateCreateCard: () -> Unit,
    onNavigateToInvitationDetail: (Long) -> Unit,
    modifier: Modifier = Modifier,
    address: AddressUiModel? = null,
) {
    MyInvitationEditScreen(
        modifier = modifier,
    )
}

@Composable
fun MyInvitationEditScreen(
    modifier: Modifier = Modifier,
) {

}
