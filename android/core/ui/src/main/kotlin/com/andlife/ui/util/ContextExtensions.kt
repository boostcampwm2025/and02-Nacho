package com.andlife.ui.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.net.toUri

fun Context.openExternalMap(
    lat: Double,
    lng: Double,
    label: String,
    onFail: () -> Unit,
) {
    val uri = "geo:$lat,$lng?q=${Uri.encode(label)}".toUri()
    val mapIntent = Intent(Intent.ACTION_VIEW, uri)

    try {
        startActivity(mapIntent)
    } catch (e: Exception) {
        Log.e("openExternalMap", "지도 앱을 실행할 수 없음: ${e.message}")
        onFail()
    }
}

fun Context.shouldRequestStoragePermission(): Boolean {
    return Build.VERSION.SDK_INT <= Build.VERSION_CODES.P &&
        ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
}
