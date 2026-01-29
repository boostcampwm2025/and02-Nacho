package com.andlife.ui.component.media

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.andlife.designsystem.theme.NachoTheme

@Composable
fun MediaItemSkeleton(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.aspectRatio(1f),
        color = NachoTheme.colorScheme.iconDisabled,
        shape = NachoTheme.shapes.medium
    ){ }
}
