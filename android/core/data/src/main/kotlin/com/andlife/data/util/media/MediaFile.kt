package com.andlife.data.util.media

import com.andlife.domain.model.MediaType
import java.io.File

data class MediaFile(
    val file: File,
    val mediaType: MediaType
)
