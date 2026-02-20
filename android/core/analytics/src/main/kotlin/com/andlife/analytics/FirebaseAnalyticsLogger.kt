package com.andlife.analytics

import com.andlife.domain.util.AnalyticsEvent
import com.andlife.domain.util.AnalyticsLogger
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import javax.inject.Inject
import kotlin.collections.component1
import kotlin.collections.component2

class FirebaseAnalyticsLogger @Inject constructor(
    private val analytics: FirebaseAnalytics
) : AnalyticsLogger {
    override fun logEvent(event: AnalyticsEvent) {
        analytics.logEvent(event.eventName) {
            event.params.forEach { (key, value) ->
                param(key, value)
            }
        }
    }
}
