package com.andlife.ui.model

enum class MediaType {
    IMAGE,
    AUDIO,
    VIDEO;

    companion object {
        fun safeValueOf(type: String, default: MediaType = IMAGE): MediaType = try {
            MediaType.valueOf(type)
        } catch (e: IllegalArgumentException) {
            default
        }
    }
}
