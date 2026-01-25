package com.andlife.ui.util

import com.andlife.model.invitation.DateTimeInfo
import com.andlife.model.invitation.TimeUiModel
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toJavaLocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.datetime.todayIn

object DateTimeConstants {
    const val YEAR = "년"
    const val MONTH = "월"
    const val DAY = "일"
    const val HOUR = "시"
    const val MINUTE = "분"
    val AM_PM = listOf("오전", "오후")

    const val D_DAY_ENDED = "종료"
    const val D_DAY_TODAY = "D-Day"
    const val D_DAY_PREFIX = "D-"
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

private val dateTimeFormat =
    LocalDateTime.Format {
        year()
        char('.')
        monthNumber()
        char('.')
        dayOfMonth()
        char(' ')
        hour()
        char(':')
        minute()
    }

fun String.toDateTime(): String =
    try {
        val dateTime = LocalDateTime.parse(this)
        dateTime.format(dateTimeFormat)
    } catch (e: Exception) {
        this
    }

fun LocalDateTime.toDateTimeFormat(): String = this.format(dateTimeFormat)

fun LocalDateTime.toFullDisplayString(locale: Locale = Locale.getDefault()): String {
    val pattern = "yyyy년 M월 d일 (E) a h시"
    val formatter = DateTimeFormatter.ofPattern(pattern, locale)
    return this.toJavaLocalDateTime().format(formatter)
}

fun LocalDate.toDDayText(): String {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val daysUntil = today.daysUntil(this)
    return when {
        daysUntil < 0 -> DateTimeConstants.D_DAY_ENDED
        daysUntil == 0 -> DateTimeConstants.D_DAY_TODAY
        else -> "${DateTimeConstants.D_DAY_PREFIX}$daysUntil"
    }
}
