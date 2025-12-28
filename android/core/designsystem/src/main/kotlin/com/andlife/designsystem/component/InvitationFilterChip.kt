package com.andlife.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.andlife.designsystem.R
import com.andlife.designsystem.theme.InvitationStroke
import com.andlife.designsystem.theme.InvitationTheme

@Composable
fun InvitationFilterChip(
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
                style = InvitationTheme.typography.bodySmallRegular
            )
        },
        modifier = modifier,
        trailingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_down_16),
                contentDescription = null,
            )
        } ,
        shape = InvitationTheme.shapes.extraLarge,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = InvitationTheme.colorScheme.backgroundSecondary,
            labelColor = InvitationTheme.colorScheme.textPrimary,
            iconColor = InvitationTheme.colorScheme.iconSecondary,
            selectedContainerColor = InvitationTheme.colorScheme.backgroundSecondary,
            selectedLabelColor = InvitationTheme.colorScheme.textPrimary,
            selectedTrailingIconColor = InvitationTheme.colorScheme.iconSecondary
        ),
        border = BorderStroke(
            InvitationStroke.small,
            InvitationTheme.colorScheme.backgroundBorder
        ),
    )
}

@Preview(showBackground = true)
@Composable
fun InvitationFilterChipPreview(){
    InvitationTheme {
        InvitationFilterChip(
            label = "가까운 순",
            selected = true,
            onClick = {}
        )
    }
}