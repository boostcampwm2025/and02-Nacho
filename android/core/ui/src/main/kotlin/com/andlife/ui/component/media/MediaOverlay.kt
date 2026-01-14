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
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme

@Composable
fun MediaOverlay(
    text: String,
    modifier: Modifier = Modifier,
    shape: CornerBasedShape = NachoTheme.shapes.extraSmall,
    backgroundColor: Color = NachoTheme.colorScheme.backgroundOverlay,
    contentColor: Color = NachoTheme.colorScheme.textOnPrimary,
    textStyle: TextStyle = NachoTheme.typography.bodyMediumRegular,
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = backgroundColor,
        contentColor = contentColor,
    ) {
        Text(
            text = text,
            style = textStyle,
            modifier = Modifier.padding(horizontal = NachoSpacing.small, vertical = NachoSpacing.xSmall),
        )
    }
}

@PreviewTheme
@Composable
private fun ImageOverlayPreview() {
    NachoTheme {
        MediaOverlay(
            text = "1/10",
            shape = NachoTheme.shapes.medium,
        )
    }
}

@PreviewTheme
@Composable
private fun VideoOverlayPreview() {
    NachoTheme {
        MediaOverlay(
            text = "8:28",
        )
    }
}
