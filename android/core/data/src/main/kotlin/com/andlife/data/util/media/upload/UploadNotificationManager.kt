package com.andlife.data.util.media.upload

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject

class UploadNotificationManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {

    fun prepareChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(NotificationManager::class.java) ?: return

            val channels = listOf(
                NotificationChannel(
                    UploadNoti.CHANNEL_ID_PROGRESS,
                    UploadNoti.CHANNEL_NAME_PROGRESS,
                    NotificationManager.IMPORTANCE_LOW,
                ).apply {
                    setSound(null, null)
                    enableVibration(false)
                },
                NotificationChannel(
                    UploadNoti.CHANNEL_ID_COMPLETE,
                    UploadNoti.CHANNEL_NAME_COMPLETE,
                    NotificationManager.IMPORTANCE_DEFAULT,
                ).apply {
                    setSound(null, null)
                    setShowBadge(true)
                },
            )
            manager.createNotificationChannels(channels)
        }
    }

    fun createProgressNotification(
        workId: UUID,
        progress: Int,
        currentFileName: String? = null,
        currentIndex: Int = 0,
        totalFiles: Int = 1
    ): Notification {
        val cancelIntent = Intent(context, UploadCancelReceiver::class.java).apply {
            putExtra(UploadKey.EXTRA_WORK_ID, workId.toString())
        }

        val cancelPendingIntent = PendingIntent.getBroadcast(
            context,
            workId.hashCode(),
            cancelIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val contentText = when {
            progress <= 0 -> UploadNoti.MSG_PREPARING
            currentFileName != null && totalFiles > 1 ->
                "$currentFileName (${currentIndex + 1}/$totalFiles) - $progress%"

            currentFileName != null ->
                "$currentFileName - $progress%"

            else ->
                "$progress%"
        }

        return NotificationCompat.Builder(context, UploadNoti.CHANNEL_ID_PROGRESS)
            .setSmallIcon(R.drawable.stat_sys_upload)
            .setContentTitle(UploadNoti.TITLE_UPLOADING)
            .setContentText(contentText)
            .setProgress(100, progress, progress <= 0)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .addAction(
                R.drawable.ic_menu_close_clear_cancel,
                UploadNoti.ACTION_CANCEL,
                cancelPendingIntent,
            )
            .build()
    }

    fun createCompleteNotification(): Notification {
        return NotificationCompat.Builder(context, UploadNoti.CHANNEL_ID_COMPLETE)
            .setSmallIcon(R.drawable.stat_sys_upload_done)
            .setContentTitle(UploadNoti.TITLE_COMPLETE)
            .setContentText(UploadNoti.MSG_COMPLETE)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
    }

    fun notifyComplete(workId: UUID) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = createCompleteNotification()
        notificationManager.notify(workId.hashCode(), notification)
    }

    // 알림 갱신
    fun update(workId: UUID, notification: Notification) {
        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(workId.hashCode(), notification)
    }


}
