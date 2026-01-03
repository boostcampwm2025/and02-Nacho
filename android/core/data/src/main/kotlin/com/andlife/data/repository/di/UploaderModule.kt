package com.andlife.data.repository.di

import com.andlife.data.util.media.MediaUploader
import com.andlife.data.util.media.MediaUploaderImpl
import com.andlife.network.api.media.MediaService
import com.andlife.network.di.MediaOkHttp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import okhttp3.OkHttpClient

@Module
@InstallIn(SingletonComponent::class)
object UploaderModule {

    @Provides // TODO: Binds
    @Singleton
    fun provideMediaUploader(
        mediaService: MediaService,
        @MediaOkHttp okHttpClient: OkHttpClient
    ): MediaUploader {
        return MediaUploaderImpl(mediaService, okHttpClient)
    }
}
