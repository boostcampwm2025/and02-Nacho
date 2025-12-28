package com.andlife.designsystem.component.datepicker.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.andlife.designsystem.component.datepicker.model.DatePickerDate
import com.andlife.designsystem.component.datepicker.model.DatePickerYearMonth
import com.andlife.designsystem.component.datepicker.model.toYearMonth
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

@Composable
fun rememberDatePickerState(
    initialSelectedDate: LocalDate? = null,
    initialDisplayedMonth: DatePickerYearMonth = initialSelectedDate?.toYearMonth()
        ?: Clock.System.todayIn(TimeZone.currentSystemDefault()).toYearMonth()
): DatePickerState {
    return remember {
        DatePickerStateImpl(
            initialSelectedDate = initialSelectedDate?.let { DatePickerDate(it) },
            initialDisplayedMonth = initialDisplayedMonth
        )
    }
}
