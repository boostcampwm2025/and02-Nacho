package com.andlife.invitation_edit.section

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.andlife.designsystem.component.NachoButton
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme

@Composable
internal fun BottomBarSection(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BottomAppBar(
        modifier = modifier,
        containerColor = NachoTheme.colorScheme.backgroundPrimary,
    ) {
        NachoButton(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(NachoSpacing.large),
            onClick = onClick,
        ) {
            Text(
                text = title,
                style = NachoTheme.typography.bodyLargeSemiBold,
                color = NachoTheme.colorScheme.textOnPrimary,
            )
        }
    }
}

@Composable
@PreviewTheme
private fun BottomBarSectionPreview() {
    NachoTheme {
        BottomBarSection(
            title = "제목",
            onClick = {},
        )
    }
}
