package com.andlife.invitation_edit.section

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.andlife.designsystem.component.InvitationButton
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.InvitationSpacing
import com.andlife.designsystem.theme.InvitationTheme

@Composable
internal fun BottomBarSection(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BottomAppBar(
        modifier = modifier,
        containerColor = InvitationTheme.colorScheme.backgroundPrimary,
    ) {
        InvitationButton(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(InvitationSpacing.large),
            onClick = onClick,
        ) {
            Text(
                text = title,
                style = InvitationTheme.typography.bodyLargeSemiBold,
                color = InvitationTheme.colorScheme.textOnPrimary,
            )
        }
    }
}

@Composable
@PreviewTheme
private fun BottomBarSectionPreview() {
    InvitationTheme {
        BottomBarSection(
            title = "제목",
            onClick = {},
        )
    }
}
