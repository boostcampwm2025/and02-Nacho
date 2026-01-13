package com.andlife.designsystem.component.timepicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme

@Stable
object NachoTimePickerDefaults {
    const val MINUTE_INTERVAL_1 = 1
    const val MINUTE_INTERVAL_5 = 5
    const val MINUTE_INTERVAL_10 = 10

    val itemVerticalPadding: Dp = NachoSpacing.threeXLarge

    @Composable
    fun colors(
        selectedTextColor: Color = NachoTheme.colorScheme.textPrimary,
        unSelectedTextColor: Color = NachoTheme.colorScheme.textTertiary,
        fadeColor: Color = NachoTheme.colorScheme.backgroundPrimary,
    ): InvitationTimePickerColors =
        InvitationTimePickerColors(
            selectedTextColor = selectedTextColor,
            unSelectedTextColor = unSelectedTextColor,
            fadeColor = fadeColor,
        )

    @Composable
    fun textStyles(textStyle: TextStyle = NachoTheme.typography.headingLarge): InvitationTimePickerTextStyles =
        InvitationTimePickerTextStyles(
            textStyle = textStyle,
        )
}

@Immutable
data class InvitationTimePickerColors(
    val selectedTextColor: Color,
    val unSelectedTextColor: Color,
    val fadeColor: Color,
)

@Immutable
data class InvitationTimePickerTextStyles(
    val textStyle: TextStyle,
)
