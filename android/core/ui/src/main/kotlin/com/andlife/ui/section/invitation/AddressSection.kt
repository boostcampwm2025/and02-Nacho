package com.andlife.ui.section.invitation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import com.andlife.ui.R
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.designsystem.theme.NachoSpacing

@Composable
fun AddressSection(
    placeName: String,
    placeAddress: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(NachoTheme.colorScheme.backgroundPrimary)
            .padding(vertical = NachoSpacing.xSmall, horizontal = NachoSpacing.large),
    ) {
        IconTextRow(
            iconRes = R.drawable.ic_location_24,
            placeName = placeName,
            placeAddress = placeAddress,
        )
    }
}

@Composable
private fun IconTextRow(
    iconRes: Int,
    placeName: String,
    placeAddress: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(NachoSpacing.xSmall),
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = Color.Unspecified,
        )

        Column(
            modifier = modifier.padding(NachoSpacing.small),
            verticalArrangement = Arrangement.spacedBy(NachoSpacing.xSmall),
        ) {
            if (placeName.isNotEmpty()) {
                Text(
                    text = placeName,
                    style = NachoTheme.typography.bodyLargeRegular,
                    color = NachoTheme.colorScheme.textPrimary,
                )
            }
            Text(
                text = placeAddress,
                style = NachoTheme.typography.bodyMediumRegular,
                color = NachoTheme.colorScheme.textSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@PreviewTheme
@Composable
private fun AddressSectionPreview() {
    NachoTheme {
        AddressSection(
            placeName = "코드스쿼드",
            placeAddress = "강남대로62길 23 4층",
        )
    }
}
