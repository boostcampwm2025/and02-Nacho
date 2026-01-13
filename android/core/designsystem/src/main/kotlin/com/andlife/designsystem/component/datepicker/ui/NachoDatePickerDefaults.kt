package com.andlife.designsystem.component.datepicker.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import com.andlife.designsystem.R
import com.andlife.designsystem.theme.NachoTheme
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

@Stable
object NachoDatePickerDefaults {
    val monthSuffixRes = R.string.txt_month
    val yearSuffixRes = R.string.txt_year

    fun today(
        clock: Clock = Clock.System,
        timeZone: TimeZone = TimeZone.currentSystemDefault(),
    ): LocalDate = clock.todayIn(timeZone)

    @Composable
    fun colors(
        selectedDateColor: Color = NachoTheme.colorScheme.textSecondary,
        selectedTextColor: Color = NachoTheme.colorScheme.textOnPrimary,
        todayBackgroundColor: Color = NachoTheme.colorScheme.backgroundSecondary,
        todayTextColor: Color = NachoTheme.colorScheme.textPrimary,
        selectedMonthColor: Color = NachoTheme.colorScheme.brandPrimary,
        normalTextColor: Color = NachoTheme.colorScheme.textSecondary,
        disabledTextColor: Color = NachoTheme.colorScheme.textDisabled,
        headerTextColor: Color = NachoTheme.colorScheme.textPrimary,
        weekdayTextColor: Color = NachoTheme.colorScheme.textPrimary,
        navigationColor: Color = NachoTheme.colorScheme.textPrimary,
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
