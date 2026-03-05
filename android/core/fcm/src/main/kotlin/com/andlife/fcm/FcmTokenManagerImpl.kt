package com.andlife.fcm

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import com.andlife.domain.repository.fcm.FcmTokenRepository
import com.andlife.domain.util.FcmTokenManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FcmTokenManagerImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val fcmTokenRepository: FcmTokenRepository
) : FcmTokenManager {

    // SharedPreferences 사용하여 FCM 토큰을 로컬에 저장
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("fcm_preferences", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_FCM_TOKEN = "fcm_token"
        private const val TAG = "FcmTokenManager"
    }

    override fun initializeToken(token: String) {
        val storedToken = getStoredToken()

        if (storedToken.isNullOrEmpty() || storedToken != token) {
            Log.d(TAG, "토큰을 서버에 전송합니다: $token")
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    fcmTokenRepository.registerTokenToServer(token)
                    saveTokenLocally(token)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to register token to server", e)
                }
            }

        } else {
            Log.d(TAG, "토큰: $token")
        }
    }

    private fun getStoredToken(): String? {
        return sharedPreferences.getString(KEY_FCM_TOKEN, null)
    }

    private fun saveTokenLocally(token: String) {
        sharedPreferences.edit {
            putString(KEY_FCM_TOKEN, token)
        }
        Log.d(TAG, "로컬에 FCM 토큰 저장됨: $token")
    }
}
