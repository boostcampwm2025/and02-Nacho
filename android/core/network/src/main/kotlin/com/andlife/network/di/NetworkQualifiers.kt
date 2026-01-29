package com.andlife.network.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthInvitation

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NonAuthInvitation

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class InvitationMedia

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Kakao

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class KakaoApiKey
