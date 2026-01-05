package com.andlife.ui.component.media

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.InvitationSpacing
import com.andlife.designsystem.theme.InvitationTheme

@Composable
fun MediaOverlay(
    text: String,
    modifier: Modifier = Modifier,
    shape: CornerBasedShape = InvitationTheme.shapes.extraSmall,
    backgroundColor: Color = InvitationTheme.colorScheme.backgroundOverlay,
    contentColor: Color = InvitationTheme.colorScheme.textOnPrimary,
    textStyle: TextStyle = InvitationTheme.typography.bodyMediumRegular
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = backgroundColor,
        contentColor = contentColor
    ) {
        Text(
            text = text,
            style = textStyle,
            modifier = Modifier.padding(horizontal = InvitationSpacing.small, vertical = InvitationSpacing.xSmall)
        )
    }
}

@PreviewTheme
@Composable
fun ImageOverlayPreview() {
    InvitationTheme {
        MediaOverlay(
            text = "1/10",
            shape = InvitationTheme.shapes.medium,
        )
    }
}

@PreviewTheme
@Composable
fun VideoOverlayPreview() {
    InvitationTheme {
        MediaOverlay(
            text = "8:28"
        )
    }
}
