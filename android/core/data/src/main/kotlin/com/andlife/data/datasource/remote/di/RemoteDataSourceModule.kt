package com.andlife.data.datasource.remote.di

import com.andlife.data.datasource.remote.address.AddressRemoteDataSource
import com.andlife.data.datasource.remote.address.AddressRemoteDataSourceImpl
import com.andlife.data.datasource.remote.invitation.InvitationRemoteDataSource
import com.andlife.data.datasource.remote.invitation.InvitationRemoteDataSourceImpl
import com.andlife.data.datasource.remote.invitation.guestbook.GuestBookRemoteDataSource
import com.andlife.data.datasource.remote.invitation.guestbook.GuestBookRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RemoteDataSourceModule {
    @Binds
    @Singleton
    abstract fun bindAddressRemoteDataSource(
        addressRemoteDataSourceImpl: AddressRemoteDataSourceImpl,
    ): AddressRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindGuestBookRemoteDataSource(
        guestBookRemoteDataSourceImpl: GuestBookRemoteDataSourceImpl,
    ): GuestBookRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindRemoteDataSource(
        detailRemoteDataSourceImpl: InvitationRemoteDataSourceImpl,
    ): InvitationRemoteDataSource
}
