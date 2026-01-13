package com.andlife.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.andlife.designsystem.R
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoStroke
import com.andlife.designsystem.theme.InvitationTheme
import com.andlife.designsystem.theme.NachoTheme

@Composable
fun NachoFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = NachoTheme.typography.bodySmallMedium,
            )
        },
        modifier = modifier,
        trailingIcon = {
            Icon(
                painter = painterResource(R.drawable.ic_chevron_down_16),
                contentDescription = stringResource(R.string.des_component_filterchip),
            )
        },
        shape = NachoTheme.shapes.extraLarge,
        colors =
            FilterChipDefaults.filterChipColors(
                containerColor = NachoTheme.colorScheme.backgroundSecondary,
                labelColor = NachoTheme.colorScheme.textPrimary,
                iconColor = NachoTheme.colorScheme.iconSecondary,
                selectedContainerColor = NachoTheme.colorScheme.backgroundSecondary,
                selectedLabelColor = NachoTheme.colorScheme.textPrimary,
                selectedTrailingIconColor = NachoTheme.colorScheme.iconSecondary,
            ),
        border =
            BorderStroke(
                NachoStroke.small,
                NachoTheme.colorScheme.backgroundBorder,
            ),
    )
}

@PreviewTheme
@Composable
private fun InvitationFilterChipPreview() {
    InvitationTheme {
        NachoFilterChip(
            label = "가까운 순",
            selected = true,
            onClick = {},
        )
    }
}
