package com.andlife.domain.util

interface CrashlyticsLogger {
    fun recordException(message: String)
    fun log(message: String)
}
