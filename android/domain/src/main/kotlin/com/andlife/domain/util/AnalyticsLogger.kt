package com.andlife.domain.util

interface AnalyticsLogger {
    fun logEvent(event: AnalyticsEvent)
}
