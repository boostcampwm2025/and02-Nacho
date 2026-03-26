package com.andlife.nacho

import android.app.Activity.OVERRIDE_TRANSITION_CLOSE
import android.app.Activity.OVERRIDE_TRANSITION_OPEN
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.nacho.viewmodel.FullscreenVideoPlayerEvent
import com.andlife.nacho.viewmodel.FullscreenVideoPlayerViewModel
import com.andlife.ui.component.media.video.FullscreenVideoActivityContract
import com.andlife.ui.component.media.video.FullscreenVideoPlayerContainer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FullscreenVideoPlayerActivity : ComponentActivity() {

    private val fullscreenVideoPlayerViewModel: FullscreenVideoPlayerViewModel by viewModels()

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
                val uiState by fullscreenVideoPlayerViewModel.uiState.collectAsStateWithLifecycle()
                val player = remember(videoUrl) { fullscreenVideoPlayerViewModel.getPlayer(videoUrl) }
                val thumbnailUrl = intent.getStringExtra(FullscreenVideoActivityContract.EXTRA_THUMBNAIL_URL)
                val startBounds = FullscreenVideoActivityContract.readStartBounds(intent)
                val view = LocalView.current
                val lifecycleOwner = LocalLifecycleOwner.current

                LaunchedEffect(videoUrl) {
                    fullscreenVideoPlayerViewModel.onEvent(FullscreenVideoPlayerEvent.Initialize(videoUrl))
                }

                DisposableEffect(lifecycleOwner, videoUrl) {
                    val observer = LifecycleEventObserver { _, event ->
                        when (event) {
                            Lifecycle.Event.ON_RESUME -> {
                                fullscreenVideoPlayerViewModel.onEvent(
                                    FullscreenVideoPlayerEvent.Foreground(videoUrl)
                                )
                            }

                            Lifecycle.Event.ON_PAUSE -> {
                                fullscreenVideoPlayerViewModel.onEvent(
                                    FullscreenVideoPlayerEvent.Background(videoUrl)
                                )
                            }

                            else -> Unit
                        }
                    }

                    lifecycleOwner.lifecycle.addObserver(observer)

                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }

                DisposableEffect(view) {
                    WindowInsetsControllerCompat(window, window.decorView).apply {
                        hide(WindowInsetsCompat.Type.systemBars())
                        systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    }
                    onDispose {
                        WindowInsetsControllerCompat(window, window.decorView)
                            .show(WindowInsetsCompat.Type.systemBars())
                    }
                }

                Surface(modifier = Modifier.fillMaxSize()) {
                    FullscreenVideoPlayerContainer(
                        player = player,
                        thumbnailUrl = thumbnailUrl,
                        startBounds = startBounds,
                        isMuted = uiState.isMuted,
                        onDismiss = {
                            setResult(RESULT_OK)
                            finish()
                        },
                        onMuteToggle = {
                            fullscreenVideoPlayerViewModel.onEvent(
                                FullscreenVideoPlayerEvent.ToggleMute
                            )
                        }
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
