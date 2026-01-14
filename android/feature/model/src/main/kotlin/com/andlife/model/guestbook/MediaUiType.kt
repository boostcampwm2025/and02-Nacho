package com.andlife.model.guestbook

enum class MediaUiType {
    IMAGE,
    AUDIO,
    VIDEO;

    companion object Companion {
        fun safeValueOf(type: String, default: MediaUiType = IMAGE): MediaUiType = try {
            MediaUiType.valueOf(type)
        } catch (e: IllegalArgumentException) {
            default
        }
    }
}

