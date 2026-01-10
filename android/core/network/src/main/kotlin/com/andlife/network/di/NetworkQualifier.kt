package com.andlife.network.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Invitation

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class InvitationMedia

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Kakao

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class KakaoApiKey

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class KakaoNativeKey
