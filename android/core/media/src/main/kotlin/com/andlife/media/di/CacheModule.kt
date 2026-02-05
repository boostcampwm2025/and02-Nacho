package com.andlife.media.di

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton

@UnstableApi
@Module
@InstallIn(SingletonComponent::class)
object CacheModule {
    const val DIR_VIDEO_CACHE = "video_cache"
    const val DIR_AUDIO_CACHE = "audio_cache"
    const val DIR_STORY_CACHE = "story_cache"
    const val VIDEO_CACHE_SIZE = 300 * 1024 * 1024L
    const val AUDIO_CACHE_SIZE = 50 * 1024 * 1024L
    const val STORY_CACHE_SIZE = 100 * 1024 * 1024L

    @Provides
    @Singleton
    @VideoSimpleCache
    fun provideSimpleVideoCache(
        @ApplicationContext context: Context,
    ): Cache {
        val cacheDir = File(context.cacheDir, DIR_VIDEO_CACHE)
        val databaseProvider = StandaloneDatabaseProvider(context)
        val evictor = LeastRecentlyUsedCacheEvictor(VIDEO_CACHE_SIZE)
        return SimpleCache(cacheDir, evictor, databaseProvider)
    }

    @Provides
    @Singleton
    @VideoCacheDataSourceFactory
    fun provideVideoCacheDataSourceFactory(
        @ApplicationContext context: Context,
        @VideoSimpleCache simpleCache: Cache,
    ): CacheDataSource.Factory =
        CacheDataSource
            .Factory()
            .setCache(simpleCache)
            .setUpstreamDataSourceFactory(DefaultHttpDataSource.Factory())
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

    @Provides
    @Singleton
    @AudioSimpleCache
    fun provideSimpleAudioCache(
        @ApplicationContext context: Context,
    ): Cache {
        val cacheDir = File(context.cacheDir, DIR_AUDIO_CACHE)
        val databaseProvider = StandaloneDatabaseProvider(context)
        val evictor = LeastRecentlyUsedCacheEvictor(AUDIO_CACHE_SIZE)
        return SimpleCache(cacheDir, evictor, databaseProvider)
    }

    @Provides
    @Singleton
    @AudioCacheDataSourceFactory
    fun provideAudioCacheDataSourceFactory(
        @ApplicationContext context: Context,
        @AudioSimpleCache simpleCache: Cache,
    ): CacheDataSource.Factory =
        CacheDataSource
            .Factory()
            .setCache(simpleCache)
            .setUpstreamDataSourceFactory(DefaultHttpDataSource.Factory())
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

    @Provides
    @Singleton
    @StorySimpleCache
    fun provideStoryVideoCache(
        @ApplicationContext context: Context,
    ): Cache {
        val cacheDir = File(context.cacheDir, DIR_STORY_CACHE)
        val evictor = LeastRecentlyUsedCacheEvictor(STORY_CACHE_SIZE)
        val databaseProvider = StandaloneDatabaseProvider(context)
        return SimpleCache(cacheDir, evictor, databaseProvider)
    }

    @Provides
    @Singleton
    @StoryCacheDataSourceFactory
    fun provideStoryCacheDataSourceFactory(
        @ApplicationContext context: Context,
        @StorySimpleCache storyCache: Cache,
    ): CacheDataSource.Factory =
        CacheDataSource.Factory()
            .setCache(storyCache)
            .setUpstreamDataSourceFactory(DefaultHttpDataSource.Factory())
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
}
