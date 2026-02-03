package com.andlife.media.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationMainScope

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AutoPlayer

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class StoryPlayer
