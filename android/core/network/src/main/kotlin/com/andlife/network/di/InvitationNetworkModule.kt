package com.andlife.network.di

import com.andlife.network.BuildConfig
import com.andlife.network.api.guestbook.GuestBookService
import com.andlife.network.api.invitation.InvitationService
import com.andlife.network.api.media.MediaService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InvitationNetworkModule {
    private const val SERVER_BASE_URL = BuildConfig.SERVER_URL
    private const val HEADER_USER_ID = BuildConfig.HEADER_USER_ID
    private const val USER_ID = BuildConfig.USER_ID

    @Provides
    @Singleton
    @Invitation
    fun provideInvitationOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(UserIdInterceptor())
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
    fun provideMediaOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(loggingInterceptor)
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

    /**
     * 임시 로그인 인터셉터 - 모든 요청에 Nacho-User-Id 헤더 추가
     * TODO: 로그인 기능 추가 시 JWT 토큰을 Authorization 헤더에 추가하는 방식으로, 토큰 갱신 로직 추가 후 해당 인터셉터 제거
    **/
    private class UserIdInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request().newBuilder()
                .addHeader(HEADER_USER_ID, USER_ID)
                .build()
            return chain.proceed(request)
        }
    }
}
