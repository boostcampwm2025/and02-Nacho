package com.andlife.data.util.media

interface MediaFileProvider {
    fun createFromUri(uriString: String): MediaFile?

    fun createFromUris(uriStrings: List<String>): List<MediaFile>
}
