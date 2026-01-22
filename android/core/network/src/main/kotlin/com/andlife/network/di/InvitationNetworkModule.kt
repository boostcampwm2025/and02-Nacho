package com.andlife.network.di

import com.andlife.network.BuildConfig
import com.andlife.network.api.guestbook.GuestBookService
import com.andlife.network.api.invitation.InvitationService
import com.andlife.network.api.media.MediaService
import com.andlife.network.interceptor.AuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InvitationNetworkModule {
    private const val SERVER_BASE_URL = BuildConfig.SERVER_URL

    @Provides
    @Singleton
    @Invitation
    fun provideInvitationOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor
    ): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()

    @Provides
    @Singleton
    @Invitation
    fun provideInvitationRetrofit(
        json: Json,
        @Invitation okHttpClient: OkHttpClient,
    ): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(SERVER_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    @Provides
    @Singleton
    @InvitationMedia
    fun provideMediaOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor
    ): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    @InvitationMedia
    fun provideMediaRetrofit(
        json: Json,
        @InvitationMedia okHttpClient: OkHttpClient,
    ): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(BuildConfig.SERVER_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    @Provides
    @Singleton
    fun provideInvitationApiService(
        @InvitationMedia retrofit: Retrofit,
    ): MediaService = retrofit.create(MediaService::class.java)

    @Provides
    @Singleton
    fun provideGuestBookService(
        @Invitation retrofit: Retrofit,
    ): GuestBookService = retrofit.create(GuestBookService::class.java)

    @Provides
    @Singleton
    fun provideInvitationService(
        @Invitation retrofit: Retrofit,
    ): InvitationService = retrofit.create(InvitationService::class.java)
}
