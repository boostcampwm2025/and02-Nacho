package com.andlife.nachoserver.util

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingException
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.MessagingErrorCode
import com.google.firebase.messaging.Notification
import org.springframework.stereotype.Component

@Component
class FCMUtil {
    // 특정 FCM 토큰으로 메시지 전송
    fun sendToToken(fcmToken: String, title: String, body: String): FcmSendResult {
        val message = Message.builder()
            .setToken(fcmToken)
            .setNotification(
                Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build()
            )
            .build()

        return try {
            val response = FirebaseMessaging.getInstance().send(message)
            println("메시지 전송 성공: $response")
            FcmSendResult.SUCCESS
        } catch (e: FirebaseMessagingException) {
            when (e.messagingErrorCode) {
                MessagingErrorCode.UNREGISTERED -> {
                    println("만료된 FCM 토큰: $fcmToken")
                    FcmSendResult.UNREGISTERED
                }
                MessagingErrorCode.INVALID_ARGUMENT -> {
                    println("유효하지 않은 FCM 토큰: $fcmToken")
                    FcmSendResult.INVALID_TOKEN
                }
                else -> {
                    println("FCM 메시지 전송 에러: ${e.messagingErrorCode}, message: ${e.message}")
                    FcmSendResult.OTHER_ERROR
                }
            }
        } catch (e: Exception) {
            println("FCM 메시지 전송 에러: ${e.message}")
            FcmSendResult.OTHER_ERROR
        }
    }
}