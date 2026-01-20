package com.andlife.data.datasource.remote.invitation.guestbook

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.andlife.data.repository.guestbook.toDomain
import com.andlife.domain.model.guestbook.GuestBook
import com.andlife.domain.util.Result

class AllGuestBookPagingSource(
    private val remoteDataSource: GuestBookRemoteDataSource,
) : PagingSource<Int, GuestBook>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GuestBook> {
        val page = params.key ?: 0

        val result = remoteDataSource.getAllRelatedGuestBooks(
            page = page,
            size = params.loadSize,
        )

        return when (result) {
            is Result.Success -> {
                val response = result.data
                LoadResult.Page(
                    data = response.content.map { it.toDomain() },
                    prevKey = if (page == 0) null else page - 1,
                    nextKey = if (response.meta.isEnd || response.content.isEmpty()) null else page + 1,
                )
            }
            is Result.Error -> {
                LoadResult.Error(
                    Exception("${result.error}: ${result.message}"),
                )
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, GuestBook>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
