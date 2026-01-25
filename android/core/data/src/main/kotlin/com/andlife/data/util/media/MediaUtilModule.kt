package com.andlife.data.util.media

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MediaUtilModule { // TODO: util에 있는 모든 것들 여기에 통합해도 될 듯요.

    @Binds
    @Singleton
    abstract fun bindImageCompressor(impl: ImageCompressorImpl): ImageCompressor
}
