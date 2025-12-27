package com.andlife.designsystem.component.datepicker.model

import androidx.compose.runtime.Stable
import kotlinx.datetime.LocalDate

@Stable
data class DatePickerDate(
    val date: LocalDate,
)

fun LocalDate.toDatePickerDate(): DatePickerDate {
    return DatePickerDate(
        date = this
    )
}