package com.andlife.domain.util

import com.andlife.domain.model.guestbook.MediaFile

interface MediaFileProvider {
    fun createFromUri(uriString: String): MediaFile?

    fun createFromUris(uriStrings: List<String>): List<MediaFile>
}
