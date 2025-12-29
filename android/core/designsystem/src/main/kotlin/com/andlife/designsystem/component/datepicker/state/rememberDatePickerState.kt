package com.andlife.designsystem.component.datepicker.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import com.andlife.designsystem.component.datepicker.model.DatePickerDate
import com.andlife.designsystem.component.datepicker.ui.DatePickerMode
import kotlinx.datetime.LocalDate

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
