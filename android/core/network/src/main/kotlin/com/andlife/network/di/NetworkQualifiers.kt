package com.andlife.network.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class InvitationOkHttp

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class InvitationRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class KakaoOkHttp

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class KakaoRetrofit
