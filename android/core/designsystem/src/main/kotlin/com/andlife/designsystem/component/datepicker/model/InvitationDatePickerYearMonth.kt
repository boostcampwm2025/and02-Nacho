package com.andlife.designsystem.component.datepicker.model

import kotlinx.datetime.LocalDate

data class InvitationDatePickerYearMonth(
    val year: Int,
    val month: Int,
) {
    fun plusMonth(): InvitationDatePickerYearMonth =
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

    fun minusMonth(): InvitationDatePickerYearMonth =
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
}

// 해당 월의 첫째 날의 요일인덱스 반환: 일요일 = 0, 월요일 = 1, ...
fun InvitationDatePickerYearMonth.getFirstDayOfWeek(): Int {
    val date = LocalDate(year, month, 1)
    // DayOfWeek.ordinal: 월요일 = 0, 화요일 = 1, ...
    return (date.dayOfWeek.ordinal + 1) % 7
}

// 해당 월의 일수 반환
fun InvitationDatePickerYearMonth.getDaysCountInMonth(): Int =
    when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (isLeapYear(year)) 29 else 28
        else -> 30
    }

// 윤년 여부 판단
private fun isLeapYear(year: Int): Boolean = year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)

fun InvitationDatePickerDate.toYearMonth(): InvitationDatePickerYearMonth =
    InvitationDatePickerYearMonth(
        year = this.date.year,
        month = this.date.monthNumber,
    )
