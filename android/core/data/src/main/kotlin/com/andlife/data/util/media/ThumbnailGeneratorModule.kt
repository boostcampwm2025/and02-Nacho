package com.andlife.data.util.media

import com.andlife.domain.util.ThumbnailGenerator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ThumbnailGeneratorModule {

    @Binds
    @Singleton
    abstract fun bindThumbnailGenerator(impl: ThumbnailGeneratorImpl): ThumbnailGenerator
}
