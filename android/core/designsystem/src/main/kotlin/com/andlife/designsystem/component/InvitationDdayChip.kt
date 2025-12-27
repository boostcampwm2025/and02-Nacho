package com.andlife.designsystem.component

import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.andlife.designsystem.theme.InvitationTheme

@Composable
fun InvitationDdayChip(
    label: String,
    modifier: Modifier = Modifier
) {
    AssistChip(
        onClick = { },
        label = {
            Text(
                text = label,
                style = InvitationTheme.typography.bodySmall1
            )
        },
        modifier = modifier,
        shape = InvitationTheme.shapes.extraLarge,
        colors = AssistChipDefaults.assistChipColors(
            containerColor = InvitationTheme.colorScheme.brandLight,
            labelColor = InvitationTheme.colorScheme.brandDark
        ),
        border = null,
    )
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