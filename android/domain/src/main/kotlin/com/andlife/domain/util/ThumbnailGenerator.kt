package com.andlife.domain.util

import java.io.File

interface ThumbnailGenerator {
    suspend fun generateVideoThumbnail(
        videoUriString: String,
        timeUs: Long = 1_000_000L,
        quality: Int = 85
    ): File?
}
