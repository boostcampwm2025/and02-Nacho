package com.andlife.nacho

import android.app.Activity.OVERRIDE_TRANSITION_CLOSE
import android.app.Activity.OVERRIDE_TRANSITION_OPEN
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.media.video.AutoVideoPlayerPool
import com.andlife.ui.component.media.video.FullscreenVideoActivityContract
import com.andlife.ui.component.media.video.FullscreenVideoPlayerContainer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FullscreenVideoPlayerActivity : ComponentActivity() {

    @Inject
    lateinit var videoPlayerPool: AutoVideoPlayerPool

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        disableOpenCloseTransition()
        enableEdgeToEdge()

        val videoUrl = intent.getStringExtra(FullscreenVideoActivityContract.EXTRA_VIDEO_URL)
        if (videoUrl.isNullOrBlank()) {
            finish()
            return
        }

        setContent {
            NachoTheme {
                val player = videoPlayerPool.getPlayer(videoUrl)
                val isMuted by videoPlayerPool.isMuted.collectAsStateWithLifecycle()
                val thumbnailUrl = intent.getStringExtra(FullscreenVideoActivityContract.EXTRA_THUMBNAIL_URL)
                val startBounds = FullscreenVideoActivityContract.readStartBounds(intent)
                val view = LocalView.current

                DisposableEffect(view) {
                    WindowInsetsControllerCompat(window, window.decorView).apply {
                        hide(WindowInsetsCompat.Type.systemBars())
                        systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    }
                    onDispose {
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                        WindowInsetsControllerCompat(window, window.decorView)
                            .show(WindowInsetsCompat.Type.systemBars())
                    }
                }

                Surface(modifier = Modifier.fillMaxSize()) {
                    FullscreenVideoPlayerContainer(
                        player = player,
                        thumbnailUrl = thumbnailUrl,
                        startBounds = startBounds,
                        isMuted = isMuted,
                        onDismiss = {
                            setResult(RESULT_OK)
                            finish()
                        },
                        onMuteToggle = { videoPlayerPool.toggleMute() }
                    )
                }
            }
        }
    }
}

private fun ComponentActivity.disableOpenCloseTransition() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        overrideActivityTransition(
            OVERRIDE_TRANSITION_OPEN,
            0,
            0
        )
        overrideActivityTransition(
            OVERRIDE_TRANSITION_CLOSE,
            0,
            0
        )
    } else {
        @Suppress("DEPRECATION")
        overridePendingTransition(0, 0)
    }
}
