package com.andlife.network.model

import kotlinx.serialization.Serializable

@Serializable
data class PagingResponse<T>(
    val meta: PagingMetaResponse,
    val content: List<T>,
)

@Serializable
data class PagingMetaResponse(
    val isEnd: Boolean,
    val pageableCount: Int,
    val totalCount: Int,
    val currentPage: Int,
)
