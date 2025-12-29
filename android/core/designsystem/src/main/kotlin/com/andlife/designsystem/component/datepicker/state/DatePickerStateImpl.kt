package com.andlife.designsystem.component.datepicker.state

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import com.andlife.designsystem.component.datepicker.model.DatePickerDate
import com.andlife.designsystem.component.datepicker.model.DatePickerYearMonth
import com.andlife.designsystem.component.datepicker.model.minusMonth
import com.andlife.designsystem.component.datepicker.model.plusMonth
import com.andlife.designsystem.component.datepicker.model.toYearMonth
import com.andlife.designsystem.component.datepicker.ui.DatePickerDefaults
import com.andlife.designsystem.component.datepicker.ui.DatePickerMode
import kotlinx.datetime.LocalDate

@Stable
internal class DatePickerStateImpl(
    initialSelectedDate: DatePickerDate?,
    initialMode: DatePickerMode
) : DatePickerState {

    private val _selectedDate = mutableStateOf(initialSelectedDate)
    private val _displayedMonth = mutableStateOf(
        initialSelectedDate?.toYearMonth()
            ?: DatePickerDate(DatePickerDefaults.today()).toYearMonth()
    )
    private val _mode = mutableStateOf(initialMode)

    override val selectedDate: LocalDate?
        get() = _selectedDate.value?.date  // DatePickerDate에서 LocalDate로 변환

    override val displayedMonth: DatePickerYearMonth
        get() = _displayedMonth.value

    override val mode: DatePickerMode
        get() = _mode.value

    override fun selectDate(date: LocalDate) {
        _selectedDate.value = DatePickerDate(date)  // LocalDate에서 DatePickerDate로 변환
    }

    override fun moveToPreviousMonth() {
        _displayedMonth.value = _displayedMonth.value.minusMonth()
    }

    override fun moveToNextMonth() {
        _displayedMonth.value = _displayedMonth.value.plusMonth()
    }

    override fun moveToPreviousYear() {
        _displayedMonth.value = _displayedMonth.value.copy(year = _displayedMonth.value.year - 1)
    }

    override fun moveToNextYear() {
        _displayedMonth.value = _displayedMonth.value.copy(year = _displayedMonth.value.year + 1)
    }

    override fun showYearMonthSelector() {
        _mode.value = DatePickerMode.YEAR_MONTH
    }

    override fun showCalendar(yearMonth: DatePickerYearMonth) {
        _displayedMonth.value = yearMonth
        _mode.value = DatePickerMode.DATE
    }

    companion object {
        fun Saver(): Saver<DatePickerStateImpl, Any> = listSaver(
            save = {
                listOf(
                    it.selectedDate?.toString(),
                    it.displayedMonth.year,
                    it.displayedMonth.month,
                    it.mode
                )
            },
            restore = { value ->
                val selectedDate =
                    value[0]?.let { DatePickerDate(LocalDate.parse(it as String)) }
                val year = value[1] as Int
                val month = value[2] as Int
                val mode = value[3] as DatePickerMode
                DatePickerStateImpl(
                    initialSelectedDate = selectedDate,
                    initialMode = mode
                ).apply {
                    _displayedMonth.value = DatePickerYearMonth(year, month)
                    _mode.value = mode
                }
            }
        )
    }
}
