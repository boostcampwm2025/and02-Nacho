package com.andlife.data.datasource.remote.di

import com.andlife.data.datasource.remote.address.AddressRemoteDataSource
import com.andlife.data.datasource.remote.address.AddressRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class AddressRemoteDataSourceModule {
    @Binds
    @Singleton
    abstract fun bindAddressRemoteDataSource(
        addressRemoteDataSourceImpl: AddressRemoteDataSourceImpl,
    ): AddressRemoteDataSource
}
