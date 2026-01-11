package com.andlife.invitationzzang

import android.app.Application
import android.util.Log
import com.andlife.deeplink.DeepLinkConfig
import com.andlife.deeplink.DeepLinkManager
import com.andlife.deeplink.di.AppsFlyerDevKey
import com.andlife.deeplink.di.KakaoNativeKey
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class InvitationApplication : Application() {
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
    }

    private fun initAppsFlyer() {
        val conversionListener =
            object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(data: MutableMap<String, Any>?) {
                    data?.let {
                        val isFirstLaunch = it[DeepLinkConfig.AF_IS_FIRST_LAUNCH] as? Boolean ?: false
                        if (!isFirstLaunch) {
                            Log.d("Application AppsFlyer", "Not first launch, skipping deferred deep link")
                            return
                        }

                        val invitationId = it[DeepLinkConfig.AF_PARAM_INVITE_ID] as? String
                        if (invitationId != null) {
                            Log.d("Application AppsFlyer", "First launch - Deferred Deep Link invitation_id: $invitationId")
                            deepLinkManager.emitInvitationId(invitationId)
                        }
                    }
                }

                override fun onConversionDataFail(error: String?) {
                    Log.e("Application AppsFlyer", "Data failed: $error")
                }

                override fun onAppOpenAttribution(data: MutableMap<String, String>?) {}

                override fun onAttributionFailure(error: String?) {}
            }

        AppsFlyerLib.getInstance().apply {
            init(appsFlyerDevKey, conversionListener, this@InvitationApplication)
            start(this@InvitationApplication)
        }
    }
}
