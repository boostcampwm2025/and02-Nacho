package com.andlife.media.di

import com.andlife.media.StoryMediaPlayerPool
import com.andlife.media.StoryMediaPlayerPoolImpl
import com.andlife.media.audio.AudioPlayerManager
import com.andlife.media.audio.AudioPlayerManagerImpl
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

    @Binds
    @Singleton
    abstract fun bindAudioPlayerManager(impl: AudioPlayerManagerImpl): AudioPlayerManager

    @Binds
    @Singleton
    abstract fun bindStoryMediaPlayerPool(impl: StoryMediaPlayerPoolImpl): StoryMediaPlayerPool
}
