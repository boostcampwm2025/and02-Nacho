package com.andlife.data.datasource.remote.address

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.andlife.domain.model.address.Address
import com.andlife.domain.util.Result

class AddressSearchPagingSource(
    private val remoteDataSource: AddressRemoteDataSource,
    private val query: String,
    private val onTotalCountLoaded: (Int) -> Unit,
) : PagingSource<Int, Address>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Address> {
        if (query.isBlank()) {
            onTotalCountLoaded(0)
            return LoadResult.Page(
                data = emptyList(),
                prevKey = null,
                nextKey = null,
            )
        }

        val page = params.key ?: INITIAL_PAGE

        val result =
            remoteDataSource.searchAddress(
                query = query,
                page = page,
                size = params.loadSize,
            )

        return when (result) {
            is Result.Success -> {
                val response = result.data
                val addresses = response.documents.map { it.toDomain() }
                val nextKey = if (response.meta.isEnd) null else page + 1

                if (page == INITIAL_PAGE) {
                    onTotalCountLoaded(response.meta.totalCount)
                }

                LoadResult.Page(
                    data = addresses,
                    prevKey = if (page == INITIAL_PAGE) null else page - 1,
                    nextKey = nextKey,
                )
            }
            is Result.Error -> {
                if (page == INITIAL_PAGE) {
                    onTotalCountLoaded(0)
                }
                LoadResult.Error(
                    Exception("${result.error}: ${result.message}"),
                )
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Address>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(INITIAL_PAGE)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(INITIAL_PAGE)
        }

    companion object {
        private const val INITIAL_PAGE = 1
    }
}
