package com.andlife.fcm

import android.util.Log
import com.andlife.domain.repository.fcm.FcmTokenRepository
import com.andlife.domain.util.FcmTokenManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FcmTokenManagerImpl @Inject constructor(
    private val fcmTokenRepository: FcmTokenRepository
) : FcmTokenManager {
    companion object {
        private const val TAG = "FcmTokenManager"
    }

    override fun initializeToken(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                fcmTokenRepository.putTokenToServer(token)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to put token to server", e)
            }
        }

    }
}
