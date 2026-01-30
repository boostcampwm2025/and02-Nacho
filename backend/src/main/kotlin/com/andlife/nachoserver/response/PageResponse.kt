package com.andlife.nachoserver.response

data class PagingResponse<T>(
    val meta: PagingMetaResponse,
    val content: List<T>
) {
    companion object {
        fun <T> empty(): PagingResponse<T> = PagingResponse(
            meta = PagingMetaResponse(
                isEnd = true,
                pageableCount = 0,
                totalCount = 0,
                currentPage = 0
            ),
            content = emptyList()
        )
    }
}

data class PagingMetaResponse(
    val isEnd: Boolean,
    val pageableCount: Int,
    val totalCount: Long,
    val currentPage: Int,
)