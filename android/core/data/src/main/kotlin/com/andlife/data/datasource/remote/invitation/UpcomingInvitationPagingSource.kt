package com.andlife.data.datasource.remote.invitation

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.andlife.data.repository.invitation.mapper.toDomain
import com.andlife.domain.model.invitation.UpcomingInvitation
import com.andlife.domain.util.Result

class UpcomingInvitationPagingSource(
    private val remoteDataSource: InvitationRemoteDataSource,
    private val days: Long,
) : PagingSource<Int, UpcomingInvitation>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UpcomingInvitation> {
        val page = params.key ?: 0

        val result = remoteDataSource.getUpcomingInvitations(
            days = days,
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

    override fun getRefreshKey(state: PagingState<Int, UpcomingInvitation>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
