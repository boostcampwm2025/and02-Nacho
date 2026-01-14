package com.andlife.myinvitation.manager

import android.content.Context
import android.content.Intent
import android.util.Log
import com.andlife.deeplink.DeepLinkManager
import com.kakao.sdk.share.ShareClient
import com.kakao.sdk.template.model.Button
import com.kakao.sdk.template.model.Content
import com.kakao.sdk.template.model.FeedTemplate
import com.kakao.sdk.template.model.Link
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class KakaoShareManager @Inject constructor(
    @param:ApplicationContext
    private val context: Context,
    private val deepLinkManager: DeepLinkManager,
) {
    fun share(
        invitationId: Long,
        imageUrl: String = "https://placehold.jp/400x400.png?text=Invitation%20Image",
        title: String = "초대장이 도착했습니다! ✨\n정우의 생일 축제",
        date: String = "2026년 1월 24일(토) 오후 6시 30분",
        location: String = "그랜드 하얏트 서울",
        btnText: String = "초대장 확인하기",
    ) {
        val appsFlyerUrl = deepLinkManager.buildAppsFlyerUrl(invitationId)
        Log.d("KakaoShare", "invitationId: $invitationId, appsFlyerUrl: $appsFlyerUrl")

        val feed = FeedTemplate(
            content =
                Content(
                    title = title,
                    description = "$date\n$location",
                    imageUrl = imageUrl,
                    link = Link(
                        androidExecutionParams = emptyMap(),
                        webUrl = appsFlyerUrl,
                        mobileWebUrl = appsFlyerUrl,
                    )
                ),
            buttons =
                listOf(
                    Button(
                        title = btnText,
                        link = Link(
                            androidExecutionParams = emptyMap(),
                            webUrl = appsFlyerUrl,
                            mobileWebUrl = appsFlyerUrl,
                        )
                    )
            )
        )

        if (ShareClient.instance.isKakaoTalkSharingAvailable(context)) {
            ShareClient.instance.shareDefault(context, feed) { result, error ->
                if (error != null) {
                    Log.e("KakaoShare", "Share failed: ${error.message}", error)
                } else if (result != null) {
                    Log.d("KakaoShare", "Share success")
                    context.startActivity(result.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                }
            }
        } else {
            Log.e("KakaoShare", "KakaoTalk not available")
        }
    }
}
