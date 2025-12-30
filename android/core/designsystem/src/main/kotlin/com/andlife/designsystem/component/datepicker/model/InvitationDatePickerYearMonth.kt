package com.andlife.designsystem.component.datepicker.model

data class InvitationDatePickerYearMonth(
    val year: Int,
    val month: Int,
)

fun InvitationDatePickerYearMonth.plusMonth(): InvitationDatePickerYearMonth =
    if (month == 12) {
        InvitationDatePickerYearMonth(
            year = year + 1,
            month = 1,
        )
    } else {
        InvitationDatePickerYearMonth(
            year = year,
            month = month + 1,
        )
    }

fun InvitationDatePickerYearMonth.minusMonth(): InvitationDatePickerYearMonth =
    if (month == 1) {
        InvitationDatePickerYearMonth(
            year = year - 1,
            month = 12,
        )
    } else {
        InvitationDatePickerYearMonth(
            year = year,
            month = month - 1,
        )
    }

fun InvitationDatePickerDate.toYearMonth(): InvitationDatePickerYearMonth =
    InvitationDatePickerYearMonth(
        year = this.date.year,
        month = this.date.monthNumber,
    )
