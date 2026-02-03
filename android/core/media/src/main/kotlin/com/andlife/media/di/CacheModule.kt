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

@Module
@InstallIn(SingletonComponent::class)
object CacheModule {
    const val DIR_VIDEO_CACHE = "video_cache"
    const val DIR_STORY_CACHE = "story_cache"
    const val VIDEO_CACHE_SIZE = 300 * 1024 * 1024L
    const val STORY_CACHE_SIZE = 100 * 1024 * 1024L

    @OptIn(UnstableApi::class)
    @Provides
    @Singleton
    @AutoPlayer
    fun provideSimpleVideoCache(
        @ApplicationContext context: Context,
    ): Cache {
        val cacheDir = File(context.cacheDir, DIR_VIDEO_CACHE)
        val databaseProvider = StandaloneDatabaseProvider(context)
        val evictor = LeastRecentlyUsedCacheEvictor(VIDEO_CACHE_SIZE)
        return SimpleCache(cacheDir, evictor, databaseProvider)
    }

    @OptIn(UnstableApi::class)
    @Provides
    @Singleton
    @AutoPlayer
    fun provideCacheDataSourceFactory(
        @ApplicationContext context: Context,
        @AutoPlayer simpleCache: Cache,
    ): CacheDataSource.Factory =
        CacheDataSource
            .Factory()
            .setCache(simpleCache)
            .setUpstreamDataSourceFactory(DefaultHttpDataSource.Factory())
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

    @OptIn(UnstableApi::class)
    @Provides
    @Singleton
    @StoryPlayer
    fun provideStoryVideoCache(
        @ApplicationContext context: Context,
    ): Cache {
        val cacheDir = File(context.cacheDir, DIR_STORY_CACHE)
        val evictor = LeastRecentlyUsedCacheEvictor(STORY_CACHE_SIZE)
        val databaseProvider = StandaloneDatabaseProvider(context)
        return SimpleCache(cacheDir, evictor, databaseProvider)
    }

    @OptIn(UnstableApi::class)
    @Provides
    @Singleton
    @StoryPlayer
    fun provideStoryCacheDataSourceFactory(
        @ApplicationContext context: Context,
        @StoryPlayer storyCache: Cache,
    ): CacheDataSource.Factory =
        CacheDataSource.Factory()
            .setCache(storyCache)
            .setUpstreamDataSourceFactory(DefaultHttpDataSource.Factory())
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
}
