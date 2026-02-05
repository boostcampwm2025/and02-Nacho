package com.andlife.media.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationMainScope

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class VideoSimpleCache

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class VideoCacheDataSourceFactory

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AudioSimpleCache

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AudioCacheDataSourceFactory

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class StorySimpleCache

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class StoryCacheDataSourceFactory
