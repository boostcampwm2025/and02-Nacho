package com.andlife.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.andlife.ui.R
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

@Composable
fun LocalDateTime.toRelativeTimeString(): String {
    val now = Clock.System.now()
    val targetTime = this.toInstant(TimeZone.currentSystemDefault())
    val durationSeconds = (now - targetTime).inWholeSeconds
    val minutes = durationSeconds / 60
    val hours = minutes / 60
    val days = hours / 24
    val weeks = days / 7
    val months = days / 30
    val years = days / 365

    return when {
        durationSeconds < 60 -> stringResource(R.string.txt_just_now)
        minutes < 60 -> stringResource(R.string.format_minutes_ago, minutes)
        hours < 24 -> stringResource(R.string.format_hours_ago, hours)
        days < 7 -> stringResource(R.string.format_days_ago, days)
        weeks < 5 -> stringResource(R.string.format_weeks_ago, weeks)
        months < 12 -> stringResource(R.string.format_months_ago, months)
        else -> stringResource(R.string.format_years_ago, years)
    }
}
