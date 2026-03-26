package com.andlife.ui.component.media.video

import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.util.UnstableApi
import com.andlife.media.video.AutoVideoPlayer

@OptIn(UnstableApi::class)
@Composable
fun VideoSurface(
    currentPlayer: AutoVideoPlayer,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        factory = {
            currentPlayer.playerView.apply {
                (parent as? ViewGroup)?.removeView(this)
            }
        },
        modifier = modifier,
    )
}
