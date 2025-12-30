package com.andlife.myinvitation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.andlife.designsystem.component.InvitationButton
import com.andlife.designsystem.component.InvitationTextField
import com.andlife.ui.model.AddressUiModel

@Composable
fun MyInvitationCreateRoute(
    selectedAddress: AddressUiModel?,
    onNavigateToAddressSearch: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MyInvitationCreateScreen(
        selectedAddress = selectedAddress,
        onNavigateToAddressSearch = onNavigateToAddressSearch,
        onNavigateBack = onNavigateBack,
        modifier = modifier,
    )
}

@Composable
private fun MyInvitationCreateScreen(
    selectedAddress: AddressUiModel?,
    onNavigateToAddressSearch: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier = modifier.fillMaxSize()) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Text(text = "MyInvitationCreateScreen")

            InvitationButton(onClick = onNavigateBack) {
                Text("뒤로가기")
            }

            InvitationButton(onClick = onNavigateToAddressSearch) {
                Text("주소 검색")
            }

            InvitationTextField(
                value = "도로명 주소: ${selectedAddress?.roadAddress}\n장소명: ${selectedAddress?.placeName}",
                onValueChange = {},
                placeholder = "주소를 선택해주세요",
                singleLine = false,
                minLines = 2,
            )
        }
    }
}
