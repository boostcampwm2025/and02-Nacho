package com.andlife.data.util.media

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UploaderModule {
    @Binds
    @Singleton
    abstract fun bindMediaUploader(impl: MediaUploaderImpl): MediaUploader
}
