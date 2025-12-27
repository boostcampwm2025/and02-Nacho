package com.andlife.designsystem.component.datepicker.state

import androidx.compose.runtime.mutableStateOf
import com.andlife.designsystem.component.datepicker.model.DatePickerDate
import com.andlife.designsystem.component.datepicker.model.DatePickerYearMonth
import com.andlife.designsystem.component.datepicker.ui.DatePickerMode

internal class DatePickerStateImpl(
    initialSelectedDate: DatePickerDate?,
    initialDisplayedMonth: DatePickerYearMonth,
) : DatePickerState {

    private val _selectedDate = mutableStateOf(initialSelectedDate)
    private val _displayedMonth = mutableStateOf(initialDisplayedMonth)
    private val _mode = mutableStateOf(DatePickerMode.DATE)

    override val selectedDate: DatePickerDate?
        get() = _selectedDate.value

    override val displayedMonth: DatePickerYearMonth
        get() = _displayedMonth.value

    override val mode: DatePickerMode
        get() = _mode.value

    override fun selectDate(date: DatePickerDate) {
        _selectedDate.value = date
    }

    // DatePickerYearMonth에 plusMonths, minusMonths 확장함수 만들고 진행하기
//    override fun moveToPreviousMonth() {
//        _displayedMonth.value = _displayedMonth.value.minusMonths(1)
//    }
//
//    override fun moveToNextMonth() {
//        _displayedMonth.value = _displayedMonth.value.plusMonths(1)
//    }

    override fun showYearMonthSelector() {
        _mode.value = DatePickerMode.YEAR_MONTH
    }

    override fun showCalendar(yearMonth: DatePickerYearMonth) {
        _displayedMonth.value = yearMonth
        _mode.value = DatePickerMode.DATE
    }
}
