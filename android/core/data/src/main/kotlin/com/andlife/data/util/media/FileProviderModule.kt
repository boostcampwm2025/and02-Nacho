package com.andlife.data.util.media

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FileProviderModule {
    @Binds
    @Singleton
    abstract fun bindMediaFileProvider(mediaFileProviderImpl: MediaFileProviderImpl): MediaFileProvider
}
