package com.andlife.designsystem.component.datepicker.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.rememberSaveable
import com.andlife.designsystem.component.datepicker.model.DatePickerDate
import com.andlife.designsystem.component.datepicker.model.DatePickerYearMonth
import com.andlife.designsystem.component.datepicker.ui.DatePickerMode
import kotlinx.datetime.LocalDate

@Stable
interface DatePickerState {
    val selectedDate: LocalDate?
    val displayedMonth: DatePickerYearMonth
    val mode: DatePickerMode

    fun selectDate(date: LocalDate)

    fun moveToPreviousMonth()

    fun moveToNextMonth()

    fun moveToPreviousYear()

    fun moveToNextYear()

    fun showYearMonthSelector()

    fun showCalendar(yearMonth: DatePickerYearMonth)
}

@Composable
fun rememberDatePickerState(
    initialSelectedDate: LocalDate? = null,
    initialMode: DatePickerMode = DatePickerMode.DATE,
): DatePickerState =
    rememberSaveable(saver = DatePickerStateImpl.Saver()) {
        DatePickerStateImpl(
            initialSelectedDate = initialSelectedDate?.let { DatePickerDate(it) },
            initialMode = initialMode,
        )
    }
