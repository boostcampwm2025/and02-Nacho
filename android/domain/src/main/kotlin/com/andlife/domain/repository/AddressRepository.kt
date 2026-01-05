package com.andlife.domain.repository

import androidx.paging.PagingData
import com.andlife.domain.model.Address
import kotlinx.coroutines.flow.Flow

interface AddressRepository {
    fun searchAddress(
        query: String,
        onTotalCountLoaded: (Int) -> Unit,
    ): Flow<PagingData<Address>>
}
