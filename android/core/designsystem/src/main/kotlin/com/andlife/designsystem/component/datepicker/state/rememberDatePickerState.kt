package com.andlife.designsystem.component.datepicker.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.andlife.designsystem.component.datepicker.model.DatePickerDate
import kotlinx.datetime.LocalDate

@Composable
fun rememberDatePickerState(
    initialSelectedDate: LocalDate? = null
): DatePickerState {
    return remember {
        DatePickerStateImpl(
            initialSelectedDate = initialSelectedDate?.let { DatePickerDate(it) },
        )
    }
}
