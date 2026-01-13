package com.andlife.ui.component.media

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.media3.common.Player
import com.andlife.designsystem.theme.NachoIconSize
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.ui.R

@Composable
fun AudioPlayer(
    exoPlayer: Player,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NachoTheme.colorScheme.backgroundPrimary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(NachoIconSize.huge)
                    .clip(CircleShape)
                    .background(NachoTheme.colorScheme.iconPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_mic_24),
                    contentDescription = stringResource(R.string.desc_ic_mic),
                    tint = Color.White,
                    modifier = Modifier.size(NachoIconSize.xLarge)
                )
            }

        }
    }
}
