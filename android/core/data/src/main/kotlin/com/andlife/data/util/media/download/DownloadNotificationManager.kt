package com.andlife.data.util.media.download

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.andlife.domain.model.guestbook.MediaType
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject

class DownloadNotificationManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {

    fun prepareChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(NotificationManager::class.java) ?: return

            val channels = listOf(
                NotificationChannel(
                    DownloadNoti.CHANNEL_ID_PROGRESS,
                    DownloadNoti.CHANNEL_NAME_PROGRESS,
                    NotificationManager.IMPORTANCE_LOW,
                ).apply {
                    setSound(null, null)
                    enableVibration(false)
                },
                NotificationChannel(
                    DownloadNoti.CHANNEL_ID_COMPLETE,
                    DownloadNoti.CHANNEL_NAME_COMPLETE,
                    NotificationManager.IMPORTANCE_DEFAULT,
                ).apply {
                    setSound(null, null)
                    setShowBadge(true)
                },
            )
            manager.createNotificationChannels(channels)
        }
    }

    fun createProgressNotification(workId: UUID, fileName: String, progress: Int): Notification {
        val cancelIntent = Intent(context, DownloadCancelReceiver::class.java).apply {
            putExtra(DownloadKey.EXTRA_WORK_ID, workId.toString())
        }

        val cancelPendingIntent = PendingIntent.getBroadcast(
            context,
            workId.hashCode(),
            cancelIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        return NotificationCompat.Builder(context, DownloadNoti.CHANNEL_ID_PROGRESS)
            .setSmallIcon(R.drawable.stat_sys_download)
            .setContentTitle(DownloadNoti.TITLE_DOWNLOADING)
            .setContentText(if (progress > 0) "$progress%" else DownloadNoti.MSG_PREPARING)
            .setSubText(fileName)
            .setProgress(100, progress, progress <= 0)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .addAction(
                R.drawable.ic_menu_close_clear_cancel,
                DownloadNoti.ACTION_CANCEL,
                cancelPendingIntent,
            )
            .build()
    }

    fun notifyComplete(fileName: String, mediaType: MediaType, savedUri: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val prefs = context.getSharedPreferences(DownloadFile.PREF_NAME, Context.MODE_PRIVATE)

        val isAudio = mediaType == MediaType.AUDIO
        val notificationId =
            if (isAudio) DownloadNoti.ID_COMPLETE_AUDIO else DownloadNoti.ID_COMPLETE_VISUAL
        val countKey =
            if (isAudio) DownloadFile.KEY_COUNT_AUDIO else DownloadFile.KEY_COUNT_VISUAL

        val currentCount = synchronized(DownloadNotificationManager::class.java) {
            val activeNotification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                notificationManager.activeNotifications.any { it.id == notificationId }
            } else {
                true
            }

            val newCount = if (!activeNotification) 1 else prefs.getInt(countKey, 0) + 1
            prefs.edit().putInt(countKey, newCount).apply()
            newCount
        }

        val viewIntent = createViewIntent(mediaType, fileName, savedUri)
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            viewIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(context, DownloadNoti.CHANNEL_ID_COMPLETE)
            .setSmallIcon(R.drawable.stat_sys_download_done)
            .setContentTitle(
                if (isAudio) DownloadNoti.TITLE_COMPLETE_AUDIO else DownloadNoti.TITLE_COMPLETE_VISUAL,
            )
            .setContentText("$currentCount${DownloadNoti.MSG_COMPLETE_SUFFIX}")
            .setSubText(fileName)
            .setNumber(currentCount)
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(notificationId, notification)
        Log.d("DownloadNotificationManager", "알림 전송 완료: ID=$notificationId, 현재 카운트=$currentCount")
    }

    private fun createViewIntent(mediaType: MediaType, fileName: String, savedUri: String): Intent {
        val mimeType = resolveMimeType(fileName, mediaType)

        return when (mediaType) {
            MediaType.IMAGE, MediaType.VIDEO -> {
                Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(Uri.parse(savedUri), mimeType)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
            }

            MediaType.AUDIO -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(Uri.parse(savedUri), mimeType)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                } else {
                    Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(Uri.parse(savedUri), mimeType)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                }
            }
        }
    }

}
