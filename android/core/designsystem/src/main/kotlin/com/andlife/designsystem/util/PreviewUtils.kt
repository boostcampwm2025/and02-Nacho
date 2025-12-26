package com.andlife.designsystem.util

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    name = "LightTheme",
    group = "Theme",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Preview(
    name = "DarkTheme",
    group = "Theme",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
annotation class ThemePreview

@Preview(
    name = "Normal",
    device = Devices.PIXEL_8,
    showBackground = true
)
@Preview(
    name = "Foldable",
    device = Devices.FOLDABLE,
    showBackground = true
)
annotation class DevicePreview