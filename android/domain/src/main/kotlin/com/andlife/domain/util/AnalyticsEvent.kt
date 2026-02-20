package com.andlife.domain.util

sealed interface AnalyticsEvent {
    val eventName: String
    val params: Map<String, String>

    data class ScreenView(val screenName: String) : AnalyticsEvent {
        override val eventName: String = "screen_view"
        override val params = mapOf("screen_name" to screenName)
    }

    data class ButtonClick(val screen: Screen, val button: Button) : AnalyticsEvent {
        override val eventName = "button_click"
        override val params = mapOf(
            "screen_name" to screen.value,
            "button_name" to button.value,
        )
    }

    data class Login(val method: LoginMethod) : AnalyticsEvent {
        override val eventName = "login"
        override val params = mapOf("method" to method.value)
    }

    data class Share(
        val itemId: String,
        val method: ShareMethod,
    ) : AnalyticsEvent {
        override val eventName = "share"
        override val params = mapOf(
            "item_id" to itemId,
            "method" to method.value,
        )
    }

    data class Event(
        override val eventName: String,
        override val params: Map<String, String> = emptyMap(),
    ) : AnalyticsEvent
}
