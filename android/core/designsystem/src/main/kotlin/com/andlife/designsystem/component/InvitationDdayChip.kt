package com.andlife.designsystem.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.andlife.designsystem.theme.InvitationSpacing
import com.andlife.designsystem.theme.InvitationTheme

@Composable
fun InvitationDdayChip(
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = InvitationTheme.colorScheme.brandLight,
        contentColor = InvitationTheme.colorScheme.brandDark,
        shape = InvitationTheme.shapes.extraLarge
    ) {
        Text(
            text = label,
            style = InvitationTheme.typography.bodySmall1,
            modifier = Modifier.padding(
                horizontal = InvitationSpacing.medium,
                vertical = InvitationSpacing.xSmall
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun InvitationDdayChipPreview(){
    InvitationTheme {
        InvitationDdayChip(
            label = "D-3",
        )
    }
}