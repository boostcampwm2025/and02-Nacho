package com.andlife.data.repository.address

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.andlife.data.datasource.remote.address.AddressRemoteDataSource
import com.andlife.data.paging.AddressSearchPagingSource
import com.andlife.domain.model.Address
import com.andlife.domain.repository.AddressRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddressRepositoryImpl @Inject constructor(
    private val remoteDataSource: AddressRemoteDataSource,
) : AddressRepository {
    override fun searchAddress(query: String): Flow<PagingData<Address>> =
        Pager(
            config =
                PagingConfig(
                    pageSize = 15,
                    enablePlaceholders = false,
                    initialLoadSize = 15,
                ),
            pagingSourceFactory = {
                AddressSearchPagingSource(
                    remoteDataSource = remoteDataSource,
                    query = query,
                )
            },
        ).flow
}
