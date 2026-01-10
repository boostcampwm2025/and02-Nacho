package com.andlife.invitationzzang

import android.app.Application
import com.andlife.network.di.KakaoNativeKey
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class InvitationApplication : Application() {
    @Inject
    @KakaoNativeKey
    lateinit var kakaoNativeKey: String

    override fun onCreate() {
        super.onCreate()

        KakaoSdk.init(this, kakaoNativeKey)
    }
}
