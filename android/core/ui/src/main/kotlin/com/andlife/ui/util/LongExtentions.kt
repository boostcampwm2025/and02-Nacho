package com.andlife.ui.util

import java.util.Locale

// 시간 포맷팅 함수 (밀리초 -> 00:00)
fun Long.toDurationFormat(): String {
    val totalSeconds = this / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.US, "%02d:%02d", minutes, seconds)
}
