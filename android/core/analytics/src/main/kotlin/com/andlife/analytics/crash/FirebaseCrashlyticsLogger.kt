package com.andlife.analytics.crash

import android.content.Context
import android.content.pm.ApplicationInfo
import com.andlife.domain.util.CrashlyticsLogger
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class FirebaseCrashlyticsLogger @Inject constructor(
    private val crashlytics: FirebaseCrashlytics,
    @param:ApplicationContext private val context: Context
) : CrashlyticsLogger {
    private val isDebuggable = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

    override fun recordException(message: String) {
        if (!isDebuggable) {
            crashlytics.recordException(Exception(message))
        }
    }

    override fun log(message: String) {
        if (!isDebuggable) {
            crashlytics.log(message)
        }
    }
}

