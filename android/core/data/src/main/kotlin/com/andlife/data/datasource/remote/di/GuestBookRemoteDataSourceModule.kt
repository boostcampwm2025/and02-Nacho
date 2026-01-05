package com.andlife.data.datasource.remote.di

import com.andlife.data.datasource.remote.invitation.guestbook.GuestBookRemoteDataSource
import com.andlife.data.datasource.remote.invitation.guestbook.GuestBookRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class GuestBookRemoteDataSourceModule {
    @Binds
    @Singleton
    abstract fun bindGuestBookRemoteDataSource(
        guestBookRemoteDataSourceImpl: GuestBookRemoteDataSourceImpl,
    ): GuestBookRemoteDataSource
}
