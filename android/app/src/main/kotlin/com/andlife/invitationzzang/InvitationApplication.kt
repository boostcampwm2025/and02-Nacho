package com.andlife.invitationzzang

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import com.kakao.sdk.common.KakaoSdk
import javax.inject.Inject
import javax.inject.Named

@HiltAndroidApp
class InvitationApplication : Application() {
    @Inject
    @Named("KakaoNativeKey")
    lateinit var kakaoNativeKey: String

    override fun onCreate() {
        super.onCreate()

        KakaoSdk.init(this, kakaoNativeKey)
    }
}
