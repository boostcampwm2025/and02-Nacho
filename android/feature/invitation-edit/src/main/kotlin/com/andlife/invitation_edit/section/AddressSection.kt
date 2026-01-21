package com.andlife.invitation_edit.section

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.andlife.designsystem.component.NachoTextField
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.invitation_edit.R
import com.andlife.invitation_edit.component.FormLabel

@Composable
internal fun AddressSection(
    placeName: String,
    placeAddress: String,
    addressGuide: String,
    onChangePlaceAddress: (String) -> Unit,
    onChangeAddressGuide: (String) -> Unit,
    onNavigateToAddressSearch: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
) {
    Box(modifier = modifier.background(NachoTheme.colorScheme.backgroundPrimary)) {
        Column(
            modifier = Modifier.padding(NachoSpacing.large),
            verticalArrangement = Arrangement.spacedBy(NachoSpacing.small),
        ) {
            FormLabel(title = stringResource(R.string.txt_invitation_address))
            NachoTextField(
                value = placeName,
                onValueChange = {},
                enabled = false,
                readOnly = true,
                placeholder = stringResource(R.string.txt_address),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(R.string.desc_address),
                        tint = NachoTheme.colorScheme.brandPrimary,
                    )
                },
                modifier = Modifier.clickable(
                    enabled = !isLoading,
                    onClick = onNavigateToAddressSearch
                ),
            )
            NachoTextField(
                value = placeAddress,
                onValueChange = onChangePlaceAddress,
                enabled = !isLoading,
                placeholder = stringResource(R.string.txt_address_detail),
            )
            NachoTextField(
                value = addressGuide,
                onValueChange = onChangeAddressGuide,
                placeholder = stringResource(R.string.txt_guide_address),
                enabled = !isLoading,
                singleLine = false,
            )
        }
    }
}

@Composable
@PreviewTheme
private fun AddressSectionPreview() {
    NachoTheme {
        AddressSection(
            placeName = "코드스쿼드",
            placeAddress = "서울특별시 강남구 강남대로62길 23",
            addressGuide = "오시는 길을 입력해주세요.",
            onChangePlaceAddress = {},
            onChangeAddressGuide = {},
            onNavigateToAddressSearch = {},
        )
    }
}
