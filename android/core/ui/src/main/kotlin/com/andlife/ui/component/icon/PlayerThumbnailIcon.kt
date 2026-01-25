package com.andlife.ui.component.icon

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoIconSize
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.ui.R

@Composable
fun PlayerThumbnailIcon(
    modifier: Modifier = Modifier,
    boxSize: Dp = NachoIconSize.xLarge,
    iconSize: Dp = NachoIconSize.medium
) {
    Box(
        modifier = modifier
            .size(boxSize)
            .background(
                color = NachoTheme.colorScheme.iconSecondary.copy(alpha = 0.6f),
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_play_arrow_24),
            contentDescription = stringResource(R.string.desc_play_video),
            tint = NachoTheme.colorScheme.iconTertiary,
            modifier = Modifier.size(iconSize),
        )
    }
}

@PreviewTheme
@Composable
private fun PlayerThumbnailIconPreview() {
    NachoTheme {
        PlayerThumbnailIcon()
    }
}
