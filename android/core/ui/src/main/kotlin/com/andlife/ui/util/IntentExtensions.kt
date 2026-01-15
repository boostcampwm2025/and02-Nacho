package com.andlife.ui.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log

fun Context.openExternalMap(
    lat: Double,
    lng: Double,
    label: String,
    onFail: () -> Unit,
) {
    val uri = Uri.parse("geo:$lat,$lng?q=${Uri.encode(label)}")
    val mapIntent = Intent(Intent.ACTION_VIEW, uri)

    try {
        startActivity(mapIntent)
    } catch (e: Exception) {
        Log.e("openExternalMap", "지도 앱을 실행할 수 없음: ${e.message}")
        onFail()
    }
}
