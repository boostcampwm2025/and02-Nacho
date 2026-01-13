package com.andlife.invitation_edit.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.InvitationTheme
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.invitation_edit.R

@Composable
internal fun RemovableImage(
    imageUrl: Any,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .clip(NachoTheme.shapes.small),
        contentAlignment = Alignment.TopEnd,
    ) {
        SubcomposeAsyncImage(
            model =
                ImageRequest
                    .Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            loading = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        color = NachoTheme.colorScheme.brandPrimary,
                    )
                }
            },
            success = {
                SubcomposeAsyncImageContent()
                Box(
                    modifier =
                        Modifier
                            .align(Alignment.TopEnd)
                            .padding(NachoSpacing.xSmall)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                            .clickable { onRemoveClick() },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.desc_remove_image),
                        tint = Color.White,
                        modifier = Modifier.padding(NachoSpacing.xSmall),
                    )
                }
            },
            error = {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(NachoTheme.colorScheme.backgroundTertiary),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_error_24),
                        contentDescription = stringResource(R.string.desc_image_error),
                        tint = NachoTheme.colorScheme.brandPrimary,
                    )
                }
            },
        )
    }
}

@Composable
@PreviewTheme
private fun RemovableImagePreview() {
    InvitationTheme {
        RemovableImage(
            imageUrl = "https://picsum.photos/200",
            onRemoveClick = {},
            modifier = Modifier,
        )
    }
}
