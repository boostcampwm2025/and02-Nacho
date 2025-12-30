package com.andlife.designsystem.component.timepicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import com.andlife.designsystem.theme.InvitationSpacing
import com.andlife.designsystem.theme.InvitationTheme

@Stable
object InvitationTimePickerDefaults {

    const val MINUTE_INTERVAL_1 = 1
    const val MINUTE_INTERVAL_5 = 5
    const val MINUTE_INTERVAL_10 = 10

    val itemVerticalPadding: Dp = InvitationSpacing.threeXLarge

    @Composable
    fun colors(
        selectedTextColor: Color = InvitationTheme.colorScheme.textPrimary,
        unSelectedTextColor: Color = InvitationTheme.colorScheme.textTertiary,
        fadeColor: Color = InvitationTheme.colorScheme.backgroundPrimary,
    ): InvitationTimePickerColors =
        InvitationTimePickerColors(
            selectedTextColor = selectedTextColor,
            unSelectedTextColor = unSelectedTextColor,
            fadeColor = fadeColor,
        )

    @Composable
    fun textStyles(textStyle: TextStyle = InvitationTheme.typography.headingLarge): InvitationTimePickerTextStyles =
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
