package com.andlife.editor.di

import com.andlife.editor.util.CardConverter
import com.andlife.editor.util.CardConverterImpl
import com.andlife.invitation_card.editor.utils.ImageLoader
import com.andlife.invitation_card.editor.utils.ImageLoaderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class EditorModule {
    @Binds
    abstract fun bindImageLoader(impl: ImageLoaderImpl): ImageLoader

    @Binds
    abstract fun bindCardConverter(impl: CardConverterImpl): CardConverter
}
