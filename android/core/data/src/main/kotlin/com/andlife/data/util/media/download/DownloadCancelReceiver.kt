package com.andlife.data.util.media.download

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.WorkManager
import java.util.UUID

class DownloadCancelReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val workIdString = intent.getStringExtra(DownloadKey.EXTRA_WORK_ID) ?: return
        try {
            val workId = UUID.fromString(workIdString)
            WorkManager.getInstance(context).cancelWorkById(workId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
