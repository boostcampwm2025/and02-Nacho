package com.andlife.ui.util

import com.andlife.model.invitation.DateTimeInfo
import com.andlife.model.invitation.TimeUiModel
import kotlinx.datetime.LocalDate

object DateTimeConstants {
    const val YEAR = "년"
    const val MONTH = "월"
    const val DAY = "일"
    const val HOUR = "시"
    const val MINUTE = "분"
    val AM_PM = listOf("오전", "오후")
}

fun LocalDate.toDisplayDateString(): String {
    return "${year}${DateTimeConstants.YEAR} ${monthNumber}${DateTimeConstants.MONTH} ${dayOfMonth}${DateTimeConstants.DAY}"
}

fun TimeUiModel.toDisplayTimeString(): String {
    val period = if (hour < 12) DateTimeConstants.AM_PM[0] else DateTimeConstants.AM_PM[1]
    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }

    val minuteText = if (min == 0) "" else " ${min}${DateTimeConstants.MINUTE}"

    return "$period $displayHour${DateTimeConstants.HOUR}$minuteText"
}

fun DateTimeInfo.toDateTimeSingleLine(): String {
    return "${date.toDisplayDateString()} ${startTime.toDisplayTimeString()}"
}
