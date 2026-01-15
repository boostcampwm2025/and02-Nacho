package com.andlife.media.di

import com.andlife.media.video.AutoVideoPlayerPool
import com.andlife.media.video.AutoVideoPlayerPoolImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class PlayerModule {
    @Binds
    @Singleton
    abstract fun bindVideoPlayerPool(impl: AutoVideoPlayerPoolImpl): AutoVideoPlayerPool
}
