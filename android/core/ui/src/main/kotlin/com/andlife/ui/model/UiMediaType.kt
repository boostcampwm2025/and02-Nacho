package com.andlife.ui.model

enum class UiMediaType {
    IMAGE,
    AUDIO,
    VIDEO;

    companion object {
        fun safeValueOf(type: String, default: UiMediaType = IMAGE): UiMediaType = try {
            UiMediaType.valueOf(type)
        } catch (e: IllegalArgumentException) {
            default
        }
    }
}
