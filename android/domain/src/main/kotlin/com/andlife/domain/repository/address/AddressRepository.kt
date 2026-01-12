package com.andlife.domain.repository.address

import androidx.paging.PagingData
import com.andlife.domain.model.address.Address
import kotlinx.coroutines.flow.Flow

interface AddressRepository {
    fun searchAddress(
        query: String,
        onTotalCountLoaded: (Int) -> Unit,
    ): Flow<PagingData<Address>>
}
