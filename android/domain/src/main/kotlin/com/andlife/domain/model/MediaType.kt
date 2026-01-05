package com.andlife.domain.model

enum class MediaType(
    val folder: String,
    val contentType: String,
    val extensions: Set<String>
) {
    VIDEO("videos", "video/mp4", setOf("mp4", "mov", "avi", "mkv")),
    IMAGE("images", "image/jpeg", setOf("jpg", "jpeg", "png", "gif", "webp")),
    AUDIO("audios", "audio/mpeg", setOf("mp3", "wav", "m4a", "aac"));

    fun getExtension(): String = when (this) {
        VIDEO -> ".mp4"
        IMAGE -> ".jpg"
        AUDIO -> ".mp3"
    }

    companion object {
        fun fromExtension(extension: String): MediaType {
            val lowerExt = extension.lowercase()
            return entries.find { it.extensions.contains(lowerExt) } ?: IMAGE
        }

        fun fromMimeType(mimeType: String?): MediaType? {
            return when {
                mimeType?.startsWith("image/") == true -> IMAGE
                mimeType?.startsWith("video/") == true -> VIDEO
                mimeType?.startsWith("audio/") == true -> AUDIO
                else -> null
            }
        }
    }
}
