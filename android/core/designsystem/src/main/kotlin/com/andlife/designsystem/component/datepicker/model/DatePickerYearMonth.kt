package com.andlife.designsystem.component.datepicker.model

import kotlinx.datetime.LocalDate

data class DatePickerYearMonth(
    val year: Int,
    val month: Int
)

fun LocalDate.toYearMonth(): DatePickerYearMonth {
    return DatePickerYearMonth(
        year = this.year,
        month = this.monthNumber
    )
}