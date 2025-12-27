package com.andlife.designsystem.component.datepicker.state

import com.andlife.designsystem.component.datepicker.model.DatePickerDate
import com.andlife.designsystem.component.datepicker.model.DatePickerYearMonth
import com.andlife.designsystem.component.datepicker.ui.DatePickerMode

interface DatePickerState {
    val selectedDate: DatePickerDate?
    val displayedMonth: DatePickerYearMonth
    val mode: DatePickerMode

    fun selectDate(date: DatePickerDate)

//    fun moveToPreviousMonth()
//    fun moveToNextMonth()

    fun showYearMonthSelector()
    fun showCalendar(yearMonth: DatePickerYearMonth)

}