package com.andlife.domain.model

data class MediaFile(
    val uriString: String,
    val mediaType: MediaType,
    val fileName: String,
    val fileSize: Long,
)
