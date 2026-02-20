package com.andlife.analytics.crash

import com.andlife.domain.util.CrashLogger
import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject

class FirebaseCrashlyticsLogger @Inject constructor(
    private val crashlytics: FirebaseCrashlytics
) : CrashLogger {
    override fun recordException(message: String) {
        crashlytics.recordException(Exception(message))
    }

    override fun log(message: String) {
        crashlytics.log(message)
    }
}

