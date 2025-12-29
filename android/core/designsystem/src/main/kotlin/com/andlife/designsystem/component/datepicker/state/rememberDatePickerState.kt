package com.andlife.designsystem.component.datepicker.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.andlife.designsystem.component.datepicker.model.DatePickerDate
import com.andlife.designsystem.component.datepicker.model.DatePickerYearMonth
import com.andlife.designsystem.component.datepicker.model.toYearMonth
import com.andlife.designsystem.component.datepicker.ui.DatePickerDefaults
import kotlinx.datetime.LocalDate

@Composable
fun rememberDatePickerState(
    initialSelectedDate: LocalDate? = null,
    initialDisplayedMonth: DatePickerYearMonth = initialSelectedDate?.toYearMonth()
        ?: DatePickerDefaults.today().toYearMonth()
): DatePickerState {
    return remember {
        DatePickerStateImpl(
            initialSelectedDate = initialSelectedDate?.let { DatePickerDate(it) },
            initialDisplayedMonth = initialDisplayedMonth
        )
    }
}
