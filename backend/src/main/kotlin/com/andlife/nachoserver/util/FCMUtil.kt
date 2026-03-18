package com.andlife.nachoserver.util

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import org.springframework.stereotype.Component

@Component
class FCMUtil {
    // 특정 FCM 토큰으로 메시지 전송
    fun sendToToken(fcmToken: String, title: String, body: String) {
        val message = Message.builder()
            .setToken(fcmToken)
            .setNotification(
                Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build()
            )
            .build()

        val response = FirebaseMessaging.getInstance().send(message)
        println("Successfully sent message: $response")
    }
}