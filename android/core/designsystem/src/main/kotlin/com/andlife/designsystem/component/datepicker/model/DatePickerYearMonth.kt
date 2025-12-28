package com.andlife.designsystem.component.datepicker.model

import kotlinx.datetime.LocalDate

data class DatePickerYearMonth(
    val year: Int,
    val month: Int
)

fun DatePickerYearMonth.plusMonth(): DatePickerYearMonth {
    return if (month == 12) {
        DatePickerYearMonth(
            year = year + 1,
            month = 1
        )
    } else {
        DatePickerYearMonth(
            year = year,
            month = month + 1
        )
    }
}

fun DatePickerYearMonth.minusMonth(): DatePickerYearMonth {
    return if (month == 1) {
        DatePickerYearMonth(
            year = year - 1,
            month = 12
        )
    } else {
        DatePickerYearMonth(
            year = year,
            month = month - 1
        )
    }
}

fun LocalDate.toYearMonth(): DatePickerYearMonth {
    return DatePickerYearMonth(
        year = this.year,
        month = this.monthNumber
    )
}