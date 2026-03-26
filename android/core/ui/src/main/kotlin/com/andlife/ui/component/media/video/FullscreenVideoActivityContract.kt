package com.andlife.ui.component.media.video

import android.content.Context
import android.content.Intent
import androidx.compose.ui.geometry.Rect

object FullscreenVideoActivityContract {

    private const val FULLSCREEN_ACTIVITY_CLASS_NAME = "com.andlife.nacho.FullscreenVideoPlayerActivity"

    const val EXTRA_VIDEO_URL = "extra_video_url"
    const val EXTRA_THUMBNAIL_URL = "extra_thumbnail_url"
    const val EXTRA_START_LEFT = "extra_start_left"
    const val EXTRA_START_TOP = "extra_start_top"
    const val EXTRA_START_RIGHT = "extra_start_right"
    const val EXTRA_START_BOTTOM = "extra_start_bottom"

    fun createIntent(
        context: Context,
        videoUrl: String,
        thumbnailUrl: String?,
        startBounds: Rect?,
    ): Intent {
        return Intent().apply {
            setClassName(context.packageName, FULLSCREEN_ACTIVITY_CLASS_NAME)
            putExtra(EXTRA_VIDEO_URL, videoUrl)
            putExtra(EXTRA_THUMBNAIL_URL, thumbnailUrl)
            if (startBounds != null) {
                putExtra(EXTRA_START_LEFT, startBounds.left)
                putExtra(EXTRA_START_TOP, startBounds.top)
                putExtra(EXTRA_START_RIGHT, startBounds.right)
                putExtra(EXTRA_START_BOTTOM, startBounds.bottom)
            }
        }
    }

    fun readStartBounds(intent: Intent): Rect? {
        val hasAllBounds = intent.hasExtra(EXTRA_START_LEFT) &&
            intent.hasExtra(EXTRA_START_TOP) &&
            intent.hasExtra(EXTRA_START_RIGHT) &&
            intent.hasExtra(EXTRA_START_BOTTOM)

        if (!hasAllBounds) return null

        return Rect(
            left = intent.getFloatExtra(EXTRA_START_LEFT, 0f),
            top = intent.getFloatExtra(EXTRA_START_TOP, 0f),
            right = intent.getFloatExtra(EXTRA_START_RIGHT, 0f),
            bottom = intent.getFloatExtra(EXTRA_START_BOTTOM, 0f),
        )
    }
}
