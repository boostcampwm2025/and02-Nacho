package com.andlife.designsystem.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoStroke
import com.andlife.designsystem.theme.NachoTheme

@Composable
fun NachoDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = NachoStroke.small,
    horizontalPadding: Dp = NachoSpacing.medium,
    color: Color = NachoTheme.colorScheme.textTertiary,
) {
    HorizontalDivider(
        modifier = modifier.padding(horizontal = horizontalPadding),
        thickness = thickness,
        color = color,
    )
}
