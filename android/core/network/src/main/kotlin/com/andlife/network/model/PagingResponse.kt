package com.andlife.network.model

import kotlinx.serialization.Serializable

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class PagingResponse<T>(
    val meta: PagingMetaResponse,
    val content: List<T>,
)

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class PagingMetaResponse(
    val isEnd: Boolean,
    val pageableCount: Int,
    val totalCount: Int,
    val currentPage: Int,
)
