package com.andlife.nacho

import android.app.Application
import android.util.Log
import com.andlife.deeplink.DeepLinkConfig
import com.andlife.deeplink.DeepLinkManager
import com.andlife.deeplink.di.AppsFlyerDevKey
import com.andlife.deeplink.di.KakaoNativeKey
import com.andlife.ui.player.VideoPlayerPool
import com.appsflyer.AppsFlyerLib
import com.appsflyer.deeplink.DeepLinkResult
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class NachoApplication : Application() {
    @Inject
    @KakaoNativeKey
    lateinit var kakaoNativeKey: String

    @Inject
    @AppsFlyerDevKey
    lateinit var appsFlyerDevKey: String

    @Inject
    lateinit var deepLinkManager: DeepLinkManager

    override fun onCreate() {
        super.onCreate()

        KakaoSdk.init(this, kakaoNativeKey)
        initAppsFlyer()
        VideoPlayerPool.initializeCache(this)
    }

    private fun initAppsFlyer() {
        val appsFlyer = AppsFlyerLib.getInstance()

        appsFlyer.subscribeForDeepLink { deepLinkResult ->
            val status = deepLinkResult.status
            if (status == DeepLinkResult.Status.FOUND) {
                val deepLinkObj = deepLinkResult.deepLink
                val invitationId = deepLinkObj.getStringValue(DeepLinkConfig.AF_DEEP_LINK_SUB1)

                if (!invitationId.isNullOrEmpty()) {
                    Log.d("AppsFlyer", "invitationId: $invitationId")
                    deepLinkManager.emitInvitationId(invitationId)
                }

            } else if (status == DeepLinkResult.Status.ERROR) {
                Log.e("AppsFlyer", "DeepLink error: ${deepLinkResult.error}")
            } else {
                Log.d("AppsFlyer", "DeepLink not (Status: $status)")
            }
        }

        appsFlyer.init(appsFlyerDevKey, null, this)
        appsFlyer.start(this)
    }
}
