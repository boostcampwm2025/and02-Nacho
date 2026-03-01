package com.andlife.database.entity

import kotlinx.serialization.Serializable

@Serializable
data class MediaCache(
    val id: Long,
    val type: String,
    val url: String,
    val thumbnailUrl: String?,
    val durationSeconds: Int?,
    val displayOrder: Int,
)
