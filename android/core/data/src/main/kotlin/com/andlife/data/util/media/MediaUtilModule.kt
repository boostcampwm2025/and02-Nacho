package com.andlife.data.util.media

import com.andlife.data.util.media.download.MediaDownloaderImpl
import com.andlife.data.util.media.upload.BackgroundMediaUploaderImpl
import com.andlife.domain.util.BackgroundMediaUploader
import com.andlife.domain.util.MediaDownloader
import com.andlife.domain.util.MediaFileProvider
import com.andlife.domain.util.MediaUploader
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MediaUtilModule {

    @Binds
    @Singleton
    abstract fun bindImageCompressor(impl: ImageCompressorImpl): ImageCompressor

    @Binds
    @Singleton
    abstract fun bindMediaUploader(impl: MediaUploaderImpl): MediaUploader

    @Binds
    @Singleton
    abstract fun bindMediaDownloader(impl: MediaDownloaderImpl): MediaDownloader

    @Binds
    @Singleton
    abstract fun bindMediaFileProvider(mediaFileProviderImpl: MediaFileProviderImpl): MediaFileProvider

    @Binds
    @Singleton
    abstract fun bindBackgroundMediaUploader(impl: BackgroundMediaUploaderImpl): BackgroundMediaUploader
}
