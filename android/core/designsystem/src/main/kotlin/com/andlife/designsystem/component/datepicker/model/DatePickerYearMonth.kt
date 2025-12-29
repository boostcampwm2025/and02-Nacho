package com.andlife.designsystem.component.datepicker.model

data class DatePickerYearMonth(
    val year: Int,
    val month: Int,
)

fun DatePickerYearMonth.plusMonth(): DatePickerYearMonth =
    if (month == 12) {
        DatePickerYearMonth(
            year = year + 1,
            month = 1,
        )
    } else {
        DatePickerYearMonth(
            year = year,
            month = month + 1,
        )
    }

fun DatePickerYearMonth.minusMonth(): DatePickerYearMonth =
    if (month == 1) {
        DatePickerYearMonth(
            year = year - 1,
            month = 12,
        )
    } else {
        DatePickerYearMonth(
            year = year,
            month = month - 1,
        )
    }

fun DatePickerDate.toYearMonth(): DatePickerYearMonth =
    DatePickerYearMonth(
        year = this.date.year,
        month = this.date.monthNumber,
    )
