package com.andlife.network.interceptor

import android.util.Log
import com.andlife.network.api.auth.AuthService
import com.andlife.network.auth.AuthTokenProvider
import com.andlife.network.model.auth.RefreshTokenRequest
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class NachoAuthenticator @Inject constructor(
    private val tokenProvider: AuthTokenProvider,
    private val authService: AuthService
) : Authenticator {

    private val mutex = Mutex()

    override fun authenticate(route: Route?, response: Response): Request? {
        Log.d("NachoAuthenticator", "재시도 탐")
        if (!shouldRetry(response)) {
            return null
        }
        val originalToken = response.request.header(AUTHORIZATION)
        val newAccessToken = refreshToken(originalToken) ?: return null

        return response.request.newBuilder()
            .header(AUTHORIZATION, "$BEARER $newAccessToken")
            .build()
    }

    private fun refreshToken(originalToken: String?): String? {
        return runBlocking {
            mutex.withLock {
                val currentAccessToken = tokenProvider.getAccessToken()
                val currentTokenHeader = "$BEARER $currentAccessToken"

                if (originalToken != currentTokenHeader && currentAccessToken != null) {
                    return@withLock currentAccessToken
                }
                val refreshToken = tokenProvider.getRefreshToken() ?: return@withLock null

                try {
                    val response = authService.reissue(RefreshTokenRequest(refreshToken))
                    if (response.data != null) {
                        val newTokens = response.data
                        tokenProvider.saveTokens(newTokens.accessToken, newTokens.refreshToken)
                        newTokens.accessToken
                    } else {
                        tokenProvider.clearTokens()
                        null
                    }
                } catch (e: HttpException) {
                    if (e.code() == UNAUTHORIZED) {
                        tokenProvider.clearTokens()
                    }
                    null
                } catch (e: IOException) {
                    null
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    companion object {
        private const val UNAUTHORIZED = 401
        private const val MAX_RETRY = 2
        private const val AUTHORIZATION = "Authorization"
        private const val BEARER = "Bearer"
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }

    private fun shouldRetry(response: Response): Boolean {
        return response.code == UNAUTHORIZED && responseCount(response) < MAX_RETRY
    }
}
