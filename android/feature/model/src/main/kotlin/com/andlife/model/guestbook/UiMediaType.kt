package com.andlife.model.guestbook

enum class UiMediaType {
    IMAGE,
    AUDIO,
    VIDEO,
    ;

    companion object {
        fun safeValueOf(
            type: String,
            default: UiMediaType = IMAGE,
        ): UiMediaType =
            try {
                valueOf(type)
            } catch (e: IllegalArgumentException) {
                default
            }
    }
}
