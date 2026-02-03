package com.andlife.myinvitation.manager

import android.content.Context
import android.content.Intent
import android.util.Log
import com.andlife.deeplink.DeepLinkManager
import com.andlife.myinvitation.R
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
        title: String,
        imageUrl: String?,
        date: String,
        location: String,
    ) {
        val appsFlyerUrl = deepLinkManager.buildAppsFlyerUrl(invitationId)
        Log.d(TAG, "invitationId: $invitationId, appsFlyerUrl: $appsFlyerUrl")

        val finalImageUrl =
            if (imageUrl.isNullOrEmpty()) {
                DEFAULT_IMG
            } else {
                imageUrl
            }

        val feed =
            FeedTemplate(
                content =
                    Content(
                        title = title,
                        description = "$date\n$location",
                        imageUrl = finalImageUrl,
                        link =
                            Link(
                                androidExecutionParams = emptyMap(),
                                webUrl = appsFlyerUrl,
                                mobileWebUrl = appsFlyerUrl,
                            ),
                    ),
                buttons =
                    listOf(
                        Button(
                            title = context.getString(R.string.btn_invitation_click),
                            link =
                                Link(
                                    androidExecutionParams = emptyMap(),
                                    webUrl = appsFlyerUrl,
                                    mobileWebUrl = appsFlyerUrl,
                                ),
                        ),
                    ),
            )

        if (ShareClient.instance.isKakaoTalkSharingAvailable(context)) {
            ShareClient.instance.shareDefault(context, feed) { result, error ->
                if (error != null) {
                    Log.e(TAG, "Share failed: ${error.message}", error)
                } else if (result != null) {
                    Log.d(TAG, "Share success")
                    context.startActivity(result.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                }
            }
        } else {
            Log.e(TAG, "KakaoTalk not available")
        }
    }

    companion object {
        private const val TAG = "KakaoShare"

        const val DEFAULT_IMG = "https://github.com/boostcampwm2025/and02-boostcamp/blob/dev/android/core/ui/src/main/res/drawable/bg_thumbnail.png?raw=true"
    }
}
