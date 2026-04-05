package com.andlife.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.andlife.domain.util.FcmTokenManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NachoFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var fcmTokenManager: FcmTokenManager

    companion object {
        private const val TAG = "NachoFirebaseMessagingService"
        private const val CHANNEL_ID = "fcm_notification_channel"
        private const val CHANNEL_NAME = "FCM 알림"
        const val INVITATION_ID_KEY = "invitation_id"
    }

    // 토큰이 클라우드 서버로 등록되었을 때 호출되는 콜백
    override fun onNewToken(token: String) {
        super.onNewToken(token)

        fcmTokenManager.syncToken(token)
    }

    // 클라우드 서버에서 메시지를 전송했을 때 호출
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "Message data payload: ${remoteMessage.data}")
        Log.d(TAG, "Message notification payload: ${remoteMessage.notification}")

        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: ""
        val body = remoteMessage.notification?.body ?: remoteMessage.data["body"] ?: ""

        sendNotification(title, body, remoteMessage.data)
    }

    // TODO
    private fun sendNotification(title: String, body: String, data: Map<String, String> = emptyMap()) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val intent = packageManager.getLaunchIntentForPackage(packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            data[INVITATION_ID_KEY]?.let { putExtra(INVITATION_ID_KEY, it) }
            data.forEach { (key, value) ->
                putExtra("fcm_$key", value)
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(1, notification)  // 임시 ID로 알림 표시
    }
}
