package com.andlife.InvitationServer.response

data class PagingResponse<T>(
    val meta: PagingMetaResponse,
    val content: List<T>
)

data class PagingMetaResponse(
    val isEnd: Boolean,
    val pageableCount: Int,
    val totalCount: Long,
    val currentPage: Int,
)