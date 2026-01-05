package com.andlife.domain.model

enum class MediaType {
    IMAGE, VIDEO, AUDIO;

    companion object {
        fun from(type: String?): MediaType {
            return when (type?.uppercase()) {
                "VIDEO" -> VIDEO
                "AUDIO" -> AUDIO
                else -> IMAGE
            }
        }
    }
}
