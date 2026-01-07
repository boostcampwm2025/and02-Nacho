package com.andlife.invitationzzang.deeplink

import android.content.Context
import android.util.Log
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails

class InstallReferrerHandler(
    private val context: Context,
    private val onReferrerReceived: (String) -> Unit
) {
    private var referrerClient: InstallReferrerClient? = null

    fun startConnection() {
        referrerClient = InstallReferrerClient.newBuilder(context).build()

        referrerClient?.startConnection(object : InstallReferrerStateListener {
            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                when (responseCode) {
                    InstallReferrerClient.InstallReferrerResponse.OK -> {
                        try {
                            val response: ReferrerDetails? = referrerClient?.installReferrer
                            response?.let {
                                val referrerUrl = it.installReferrer
                                Log.d("InstallReferrer", "Referrer: $referrerUrl")

                                parseInvitationId(referrerUrl)?.let { id ->
                                    onReferrerReceived(id)
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("InstallReferrer", "Error getting referrer", e)
                        } finally {
                            endConnection()
                        }
                    }
                    InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> {
                        Log.w("InstallReferrer", "Feature not supported")
                        endConnection()
                    }
                    InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE -> {
                        Log.w("InstallReferrer", "Service unavailable")
                        endConnection()
                    }
                }
            }

            override fun onInstallReferrerServiceDisconnected() {
                Log.d("InstallReferrer", "Service disconnected")
            }
        })
    }

    private fun parseInvitationId(referrerUrl: String): String? {
        val regex = """invite_id=(\d+)""".toRegex()
        val matchResult = regex.find(referrerUrl)
        val id = matchResult?.groupValues?.get(1)

        Log.d("InstallReferrer", "Parsed ID: $id from referrer: $referrerUrl")
        return id
    }

    private fun endConnection() {
        referrerClient?.endConnection()
        referrerClient = null
    }
}
