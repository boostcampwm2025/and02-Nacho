package com.andlife.designsystem.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.InvitationTheme
import com.andlife.designsystem.theme.NachoTheme

@Composable
fun NachoDdayChip(
    label: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = NachoTheme.colorScheme.brandLight,
        contentColor = NachoTheme.colorScheme.brandDark,
        shape = NachoTheme.shapes.extraLarge,
    ) {
        Text(
            text = label,
            style = NachoTheme.typography.bodySmallSemiBold,
            modifier =
                Modifier
                    .padding(
                        horizontal = NachoSpacing.medium,
                        vertical = NachoSpacing.xSmall,
                    ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@PreviewTheme
@Composable
private fun InvitationDdayChipPreview() {
    InvitationTheme {
        NachoDdayChip(
            label = "D-3",
        )
    }
}
