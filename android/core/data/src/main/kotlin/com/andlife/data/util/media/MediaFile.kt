package com.andlife.data.util.media

import com.andlife.domain.model.MediaType

data class MediaFile(
    val uriString: String,
    val mediaType: MediaType,
    val fileName: String,
    val fileSize: Long
)
