package com.andlife.domain.util.media

import com.andlife.domain.model.MediaFile

interface MediaFileProvider {
    fun createFromUri(uriString: String): MediaFile?

    fun createFromUris(uriStrings: List<String>): List<MediaFile>
}
