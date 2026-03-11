package com.andlife.nacho

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.andlife.deeplink.DeepLinkConfig
import com.andlife.deeplink.DeepLinkManager
import com.andlife.deeplink.di.AppsFlyerDevKey
import com.andlife.deeplink.di.KakaoNativeKey
import com.andlife.domain.util.AnalyticsEvent
import com.andlife.domain.util.AnalyticsLogger
import com.andlife.domain.util.FcmTokenManager
import com.andlife.nacho.di.NaverMapClientId
import com.appsflyer.AppsFlyerLib
import com.appsflyer.deeplink.DeepLinkResult
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.sdk.common.KakaoSdk
import com.naver.maps.map.NaverMapSdk
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class NachoApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    @KakaoNativeKey
    lateinit var kakaoNativeKey: String

    @Inject
    @AppsFlyerDevKey
    lateinit var appsFlyerDevKey: String

    @Inject
    @NaverMapClientId
    lateinit var naverMapClientId: String

    @Inject
    lateinit var deepLinkManager: DeepLinkManager

    @Inject
    lateinit var fcmTokenManager: FcmTokenManager

    @Inject
    lateinit var analyticsLogger: AnalyticsLogger

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        initAppsFlyer()

        KakaoSdk.init(this, kakaoNativeKey)
        NaverMapSdk.getInstance(this).client =
            NaverMapSdk.NcpKeyClient(naverMapClientId)

        initFcmToken()
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
                    analyticsLogger.logEvent(
                        AnalyticsEvent.Event(
                            "Deeplink",
                            mapOf(
                                "invitation_id" to invitationId
                            )
                        )
                    )
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

    private fun initFcmToken() {
        // FCM 토큰 초기화
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            val token = task.result
            Log.d("FCM", "FCM Registration token: $token")
            fcmTokenManager.initializeToken(token)
        })
    }
}
