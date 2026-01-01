package com.andlife.domain.usecase

import androidx.paging.PagingData
import com.andlife.domain.model.Address
import com.andlife.domain.repository.AddressRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchAddressUseCase @Inject constructor(
    private val addressRepository: AddressRepository
) {
    operator fun invoke(query:String): Flow<PagingData<Address>> =
        addressRepository.searchAddress(query)
}
