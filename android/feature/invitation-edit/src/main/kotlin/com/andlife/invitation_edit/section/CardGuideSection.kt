package com.andlife.invitation_edit.section

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.invitation_edit.R
import com.andlife.designsystem.R as designR

@Composable
fun CardGuideSection(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(NachoTheme.colorScheme.backgroundPrimary)
            .padding(NachoSpacing.large),
        verticalArrangement = Arrangement.spacedBy(NachoSpacing.small),
    ) {
        Text(
            text = stringResource(R.string.txt_card),
            style = NachoTheme.typography.bodyMediumSemiBold,
            color = NachoTheme.colorScheme.textPrimary,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = NachoSpacing.threeXLarge),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(NachoSpacing.medium, Alignment.CenterHorizontally),
        ) {
            Icon(
                painter = painterResource(designR.drawable.ic_info_24),
                contentDescription = null,
                tint = NachoTheme.colorScheme.textTertiary,
            )

            Text(
                text = stringResource(R.string.txt_edit_card_empty_guide),
                style = NachoTheme.typography.bodyMediumMedium,
                color = NachoTheme.colorScheme.textTertiary,
            )
        }
    }
}
