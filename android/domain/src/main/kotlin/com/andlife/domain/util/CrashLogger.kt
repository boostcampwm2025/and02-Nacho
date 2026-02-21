package com.andlife.domain.util

interface CrashLogger {
    fun recordException(message: String)
    fun log(message: String)
}
