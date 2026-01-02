package com.andlife.designsystem.component.datepicker.state

import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import com.andlife.designsystem.component.datepicker.model.InvitationDatePickerCell
import com.andlife.designsystem.component.datepicker.model.InvitationDatePickerDate
import com.andlife.designsystem.component.datepicker.model.InvitationDatePickerYearMonth
import com.andlife.designsystem.component.datepicker.model.getDaysCountInMonth
import com.andlife.designsystem.component.datepicker.model.toYearMonth
import com.andlife.designsystem.component.datepicker.ui.InvitationDatePickerDefaults
import com.andlife.designsystem.component.datepicker.ui.InvitationDatePickerMode
import kotlinx.datetime.LocalDate

@Stable
internal class InvitationDatePickerStateImpl(
    initialSelectedDate: InvitationDatePickerDate?,
    initialMode: InvitationDatePickerMode,
) : InvitationDatePickerState {
    private val _selectedDate = mutableStateOf(initialSelectedDate)
    private val _displayedMonth =
        mutableStateOf(
            initialSelectedDate?.toYearMonth()
                ?: InvitationDatePickerDate(InvitationDatePickerDefaults.today()).toYearMonth(),
        )
    private val _mode = mutableStateOf(initialMode)

    override val selectedDate: LocalDate?
        get() = _selectedDate.value?.date // InvitationDatePickerDate에서 LocalDate로 변환

    override val displayedMonth: InvitationDatePickerYearMonth
        get() = _displayedMonth.value

    override val mode: InvitationDatePickerMode
        get() = _mode.value

    override val dateCells: List<InvitationDatePickerCell> by derivedStateOf {
        val daysCountInMonth = _displayedMonth.value.getDaysCountInMonth()
        val today = InvitationDatePickerDefaults.today()

        (1..daysCountInMonth).map { day ->
            val date =
                InvitationDatePickerDate(
                    LocalDate(_displayedMonth.value.year, _displayedMonth.value.month, day),
                )

            InvitationDatePickerCell(
                date = date,
                day = day,
                isSelected = _selectedDate.value?.date == date.date,
                isToday = date.date == today,
                isDisabled = date.date < today,
            )
        }
    }

    override fun selectDate(date: LocalDate) {
        _selectedDate.value = InvitationDatePickerDate(date) // LocalDate에서 InvitationDatePickerDate로 변환
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
        _mode.value = InvitationDatePickerMode.YEAR_MONTH
    }

    override fun showCalendar(yearMonth: InvitationDatePickerYearMonth) {
        _displayedMonth.value = yearMonth
        _mode.value = InvitationDatePickerMode.DATE
    }

    companion object {
        fun Saver(): Saver<InvitationDatePickerStateImpl, Any> =
            listSaver(
                save = {
                    listOf(
                        it.selectedDate?.toString(),
                        it.displayedMonth.year,
                        it.displayedMonth.month,
                        it.mode,
                    )
                },
                restore = { value ->
                    val selectedDate =
                        value[0]?.let { InvitationDatePickerDate(LocalDate.parse(it as String)) }
                    val year = value[1] as Int
                    val month = value[2] as Int
                    val mode = value[3] as InvitationDatePickerMode
                    InvitationDatePickerStateImpl(
                        initialSelectedDate = selectedDate,
                        initialMode = mode,
                    ).apply {
                        _displayedMonth.value = InvitationDatePickerYearMonth(year, month)
                        _mode.value = mode
                    }
                },
            )
    }
}
