package com.andlife.data.repository.di

import com.andlife.data.util.media.MediaUploader
import com.andlife.data.util.media.MediaUploaderImpl
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
