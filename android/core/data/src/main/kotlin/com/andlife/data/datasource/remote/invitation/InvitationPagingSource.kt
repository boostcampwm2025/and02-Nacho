package com.andlife.data.datasource.remote.invitation

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.andlife.data.repository.invitation.mapper.toDomain
import com.andlife.domain.model.invitation.InvitationStatus
import com.andlife.domain.model.invitation.InvitationSummary
import com.andlife.domain.model.invitation.SortDirection
import com.andlife.domain.util.Result

class InvitationPagingSource(
    private val remoteDataSource: InvitationRemoteDataSource,
    private val status: InvitationStatus,
    private val sortType: SortDirection,
) : PagingSource<Int, InvitationSummary>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, InvitationSummary> {
        val page = params.key ?: 0

        val result = remoteDataSource.getParticipantInvitations(
            status = status,
            page = page,
            sortType = sortType,
            size = params.loadSize
        )

        return when (result) {
            is Result.Success -> {
                val pagingResponse = result.data
                LoadResult.Page(
                    data = pagingResponse.content.map { it.toDomain() },
                    prevKey = if (page == 0) null else page - 1,
                    nextKey = if (pagingResponse.meta.isEnd) null else page + 1
                )
            }
            is Result.Error -> {
                LoadResult.Error(
                    Exception("${result.error}: ${result.message}")
                )
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, InvitationSummary>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

}
