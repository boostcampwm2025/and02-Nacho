package com.andlife.designsystem.component.datepicker.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import com.andlife.designsystem.R
import com.andlife.designsystem.theme.InvitationTheme
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

@Stable
object InvitationDatePickerDefaults {
    val monthSuffixRes = R.string.txt_month
    val yearSuffixRes = R.string.txt_year

    fun today(
        clock: Clock = Clock.System,
        timeZone: TimeZone = TimeZone.currentSystemDefault(),
    ): LocalDate = clock.todayIn(timeZone)

    @Composable
    fun colors(
        selectedDateColor: Color = InvitationTheme.colorScheme.textSecondary,
        selectedTextColor: Color = InvitationTheme.colorScheme.textOnPrimary,
        todayBackgroundColor: Color = InvitationTheme.colorScheme.backgroundSecondary,
        todayTextColor: Color = InvitationTheme.colorScheme.textPrimary,
        selectedMonthColor: Color = InvitationTheme.colorScheme.brandPrimary,
        normalTextColor: Color = InvitationTheme.colorScheme.textSecondary,
        disabledTextColor: Color = InvitationTheme.colorScheme.textDisabled,
        headerTextColor: Color = InvitationTheme.colorScheme.textPrimary,
        weekdayTextColor: Color = InvitationTheme.colorScheme.textPrimary,
        navigationColor: Color = InvitationTheme.colorScheme.textPrimary,
    ): InvitationDatePickerColors =
        InvitationDatePickerColors(
            selectedDateColor = selectedDateColor,
            selectedTextColor = selectedTextColor,
            todayBackgroundColor = todayBackgroundColor,
            todayTextColor = todayTextColor,
            selectedMonthColor = selectedMonthColor,
            normalTextColor = normalTextColor,
            disabledTextColor = disabledTextColor,
            headerTextColor = headerTextColor,
            weekdayTextColor = weekdayTextColor,
            navigationColor = navigationColor,
        )
}

@Immutable
data class InvitationDatePickerColors(
    val selectedDateColor: Color,
    val selectedTextColor: Color,
    val todayBackgroundColor: Color,
    val todayTextColor: Color,
    val selectedMonthColor: Color,
    val normalTextColor: Color,
    val disabledTextColor: Color,
    val headerTextColor: Color,
    val weekdayTextColor: Color,
    val navigationColor: Color,
)
