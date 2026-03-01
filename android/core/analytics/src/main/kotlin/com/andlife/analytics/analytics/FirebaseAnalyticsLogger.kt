package com.andlife.analytics.analytics

import android.content.Context
import android.content.pm.ApplicationInfo
import com.andlife.domain.util.AnalyticsEvent
import com.andlife.domain.util.AnalyticsLogger
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.collections.component1
import kotlin.collections.component2

class FirebaseAnalyticsLogger @Inject constructor(
    private val analytics: FirebaseAnalytics,
    @param:ApplicationContext private val context: Context
) : AnalyticsLogger {
    private val isDebuggable = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

    override fun logEvent(event: AnalyticsEvent) {
        if (!isDebuggable) {
            analytics.logEvent(event.eventName) {
                event.params.forEach { (key, value) ->
                    param(key, value)
                }
            }
        }
    }
}
