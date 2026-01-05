package com.andlife.data.repository.di

import com.andlife.data.repository.guestbook.GuestBookRepositoryImpl
import com.andlife.domain.repository.guestbook.GuestBookRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class GuestBookRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindGuestBookRepository(
        guestBookRepositoryImpl: GuestBookRepositoryImpl
    ): GuestBookRepository
}
