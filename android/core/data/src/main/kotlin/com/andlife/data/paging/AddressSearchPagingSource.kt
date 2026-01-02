package com.andlife.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.andlife.data.datasource.remote.address.AddressRemoteDataSource
import com.andlife.data.mapper.toDomain
import com.andlife.domain.model.Address
import retrofit2.HttpException
import java.io.IOException

class AddressSearchPagingSource(
    private val remoteDataSource: AddressRemoteDataSource,
    private val query: String,
) : PagingSource<Int, Address>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Address> {
        if (query.isBlank()) {
            return LoadResult.Page(
                data = emptyList(),
                prevKey = null,
                nextKey = null,
            )
        }

        val page = params.key ?: INITIAL_PAGE

        return try {
            val response =
                remoteDataSource.searchAddress(
                    query = query,
                    page = page,
                    size = params.loadSize,
                )
            val addresses = response.documents.map { it.toDomain() }
            val nextKey = if (response.meta.isEnd) null else page + 1

            LoadResult.Page(
                data = addresses,
                prevKey = if (page == INITIAL_PAGE) null else page - 1,
                nextKey = nextKey,
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        } catch (e: Exception) {
            LoadResult.Error(e)
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
