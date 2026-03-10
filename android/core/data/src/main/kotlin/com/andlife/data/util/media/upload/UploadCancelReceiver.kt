package com.andlife.data.util.media.upload

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.WorkManager
import java.util.UUID

class UploadCancelReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val workIdString = intent?.getStringExtra(UploadKey.EXTRA_WORK_ID)
        if (workIdString.isNullOrEmpty() || context == null) return

        try {
            val workId = UUID.fromString(workIdString)
            WorkManager.getInstance(context).cancelWorkById(workId)
            Log.d("UploadCancelReceiver", "업로드 작업 취소: $workId")
        } catch (e: Exception) {
            Log.e("UploadCancelReceiver", "업로드 취소 실패", e)
        }
    }
}