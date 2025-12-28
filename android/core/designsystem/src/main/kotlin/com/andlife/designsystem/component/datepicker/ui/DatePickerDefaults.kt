package com.andlife.designsystem.component.datepicker.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import com.andlife.designsystem.theme.InvitationTheme

@Stable
object DatePickerDefaults {

    @Composable
    fun colors(
        selectedDateColor: Color = InvitationTheme.colorScheme.textSecondary,
        selectedTextColor: Color = InvitationTheme.colorScheme.textOnPrimary,
        todayBackgroundColor: Color = InvitationTheme.colorScheme.textDisabled,
        todayTextColor: Color = InvitationTheme.colorScheme.textPrimary,
        selectedMonthColor: Color = InvitationTheme.colorScheme.brandPrimary,
        normalTextColor: Color = InvitationTheme.colorScheme.textSecondary,
        disabledTextColor: Color = InvitationTheme.colorScheme.textDisabled,
        headerTextColor: Color = InvitationTheme.colorScheme.textPrimary,
        weekdayTextColor: Color = InvitationTheme.colorScheme.textPrimary,
        navigationColor: Color = InvitationTheme.colorScheme.textPrimary
    ): DatePickerColors {
        return DatePickerColors(
            selectedDateColor = selectedDateColor,
            selectedTextColor = selectedTextColor,
            todayBackgroundColor = todayBackgroundColor,
            todayTextColor = todayTextColor,
            selectedMonthColor = selectedMonthColor,
            normalTextColor = normalTextColor,
            disabledTextColor = disabledTextColor,
            headerTextColor = headerTextColor,
            weekdayTextColor = weekdayTextColor,
            navigationColor = navigationColor
        )
    }
}

@Immutable
data class DatePickerColors(
    val selectedDateColor: Color,
    val selectedTextColor: Color,
    val todayBackgroundColor: Color,
    val todayTextColor: Color,
    val selectedMonthColor: Color,
    val normalTextColor: Color,
    val disabledTextColor: Color,
    val headerTextColor: Color,
    val weekdayTextColor: Color,
    val navigationColor: Color
)