package com.andlife.ui.component.media.video

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import coil3.compose.AsyncImage
import com.andlife.ui.R
import com.andlife.ui.component.icon.PlayerThumbnailIcon

@Composable
fun VideoThumbnail(
    thumbnailUrl: String?,
    onPlayVideoClick: () -> Unit,
    showPlayButton: Boolean = true,
    enableClick: Boolean = true,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = if (enableClick) {
            modifier.clickable { onPlayVideoClick() }
        } else {
            modifier
        }
    ) {
        AsyncImage(
            model = thumbnailUrl,
            contentDescription = stringResource(R.string.desc_video_thumbnail),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit,
        )
        if (showPlayButton) {
            PlayerThumbnailIcon(modifier = Modifier.align(Alignment.Center))
        }
    }
}
