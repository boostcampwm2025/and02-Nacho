package com.andlife.designsystem.component.datepicker.state

import androidx.compose.runtime.Stable
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