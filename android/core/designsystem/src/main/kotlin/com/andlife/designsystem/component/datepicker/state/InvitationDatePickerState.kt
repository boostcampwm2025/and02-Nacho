package com.andlife.designsystem.component.datepicker.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.rememberSaveable
import com.andlife.designsystem.component.datepicker.model.InvitationDatePickerDate
import com.andlife.designsystem.component.datepicker.model.InvitationDatePickerYearMonth
import com.andlife.designsystem.component.datepicker.ui.InvitationDatePickerMode
import kotlinx.datetime.LocalDate

@Stable
interface InvitationDatePickerState {
    val selectedDate: LocalDate?
    val displayedMonth: InvitationDatePickerYearMonth
    val mode: InvitationDatePickerMode

    fun selectDate(date: LocalDate)

    fun moveToPreviousMonth()

    fun moveToNextMonth()

    fun moveToPreviousYear()

    fun moveToNextYear()

    fun showYearMonthSelector()

    fun showCalendar(yearMonth: InvitationDatePickerYearMonth)
}

@Composable
fun rememberInvitationDatePickerState(
    initialSelectedDate: LocalDate? = null,
    initialMode: InvitationDatePickerMode = InvitationDatePickerMode.DATE,
): InvitationDatePickerState =
    rememberSaveable(saver = InvitationDatePickerStateImpl.Saver()) {
        InvitationDatePickerStateImpl(
            initialSelectedDate = initialSelectedDate?.let { InvitationDatePickerDate(it) },
            initialMode = initialMode,
        )
    }
