package com.andlife.designsystem.component.datepicker.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.rememberSaveable
import com.andlife.designsystem.component.datepicker.model.NachoDatePickerCell
import com.andlife.designsystem.component.datepicker.model.NachoDatePickerDate
import com.andlife.designsystem.component.datepicker.model.NachoDatePickerYearMonth
import com.andlife.designsystem.component.datepicker.ui.InvitationDatePickerMode
import kotlinx.datetime.LocalDate

@Stable
interface NachoDatePickerState {
    val selectedDate: LocalDate?
    val displayedMonth: NachoDatePickerYearMonth
    val mode: InvitationDatePickerMode
    val dateCells: List<NachoDatePickerCell>

    fun selectDate(date: LocalDate)

    fun moveToPreviousMonth()

    fun moveToNextMonth()

    fun moveToPreviousYear()

    fun moveToNextYear()

    fun showYearMonthSelector()

    fun showCalendar(yearMonth: NachoDatePickerYearMonth)
}

@Composable
fun rememberInvitationDatePickerState(
    initialSelectedDate: LocalDate? = null,
    initialMode: InvitationDatePickerMode = InvitationDatePickerMode.DATE,
): NachoDatePickerState =
    rememberSaveable(saver = NachoDatePickerStateImpl.Saver()) {
        NachoDatePickerStateImpl(
            initialSelectedDate = initialSelectedDate?.let { NachoDatePickerDate(it) },
            initialMode = initialMode,
        )
    }
