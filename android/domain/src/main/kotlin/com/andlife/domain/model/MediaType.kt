package com.andlife.domain.model

enum class MediaType(val contentType: String) {
    IMAGE("image/jpeg"),
    VIDEO("video/mp4"),
    AUDIO("audio/mpeg");

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
