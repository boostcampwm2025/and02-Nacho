package com.andlife.deeplink

import android.content.Context
import android.util.Log
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class InstallReferrerHandler @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val deepLinkManager: DeepLinkManager,
) {
    private var referrerClient: InstallReferrerClient? = null

    fun startConnection() {
        if (referrerClient != null) return

        referrerClient = InstallReferrerClient.newBuilder(context).build()
        referrerClient?.startConnection(
            object : InstallReferrerStateListener {
                override fun onInstallReferrerSetupFinished(responseCode: Int) {
                    when (responseCode) {
                        InstallReferrerClient.InstallReferrerResponse.OK -> {
                            try {
                                val response: ReferrerDetails? = referrerClient?.installReferrer
                                response?.let {
                                    val referrerUrl = it.installReferrer
                                    Log.d("InstallReferrer", "Referrer: $referrerUrl")

                                    InstallReferrerParser.parseInvitationId(referrerUrl)?.let { id ->
                                        deepLinkManager.emitInvitationId(id)
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("InstallReferrer", "Error getting referrer", e)
                            } finally {
                                endConnection()
                            }
                        }

                        else -> {
                            Log.w("InstallReferrer", "Response Code: $responseCode")
                            endConnection()
                        }
                    }
                }

                override fun onInstallReferrerServiceDisconnected() {
                    Log.d("InstallReferrer", "Service disconnected")
                }
            },
        )
    }

    fun endConnection() {
        try {
            referrerClient?.endConnection()
        } catch (e: Exception) {
            Log.e("InstallReferrer", "Error ending connection", e)
        } finally {
            referrerClient = null
        }
    }
}
