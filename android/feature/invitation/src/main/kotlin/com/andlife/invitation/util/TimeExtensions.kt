package com.andlife.invitation.util

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.char

private val dateTimeFormat = LocalDateTime.Format {
    year(); char('.')
    monthNumber(); char('.')
    dayOfMonth(); char(' ')
    hour(); char(':'); minute()
}

fun String.toDateTime(): String {
    return try {
        val dateTime = LocalDateTime.parse(this)
        dateTime.format(dateTimeFormat)
    } catch (e: Exception) {
        this
    }
}

fun LocalDateTime.toDateTimeFormat(): String {
    return this.format(dateTimeFormat)
}
