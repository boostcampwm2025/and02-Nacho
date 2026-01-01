package com.andlife.data.repository.di

import com.andlife.data.datasource.remote.address.AddressRemoteDataSource
import com.andlife.data.repository.address.AddressRepositoryImpl
import com.andlife.domain.repository.AddressRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AddressRepositoryModule {
    @Provides
    @Singleton
    fun provideAddressRepository(
        remoteDataSource: AddressRemoteDataSource,
    ): AddressRepository =
        AddressRepositoryImpl(
            remoteDataSource = remoteDataSource,
        )
}
