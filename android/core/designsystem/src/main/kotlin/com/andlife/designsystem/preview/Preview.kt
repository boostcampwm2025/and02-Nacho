package com.andlife.designsystem.preview

import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_TYPE_NORMAL
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
    showBackground = true,
    uiMode = UI_MODE_TYPE_NORMAL
)
@Preview(
    name = "Foldable",
    showBackground = true,
    device = Devices.FOLDABLE,
)
annotation class DevicePreview