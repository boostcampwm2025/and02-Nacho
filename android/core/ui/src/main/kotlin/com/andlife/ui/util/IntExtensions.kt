package com.andlife.ui.util

fun Int.toFormatDuration(): String {
    val h = this / 3600
    val m = (this % 3600) / 60
    val s = this % 60

    return if (h > 0) {
        "%02d:%02d:%02d".format(h, m, s)
    } else {
        "%02d:%02d".format(m, s)
    }
}
