package com.andlife.ui.section.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.ui.R
import com.andlife.ui.component.media.MediaOverlay
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
fun ImageSection(
    imageUrls: ImmutableList<String>,
    onImageClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(NachoSpacing.medium),
    ) {
        if (imageUrls.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(R.drawable.bg_thumbnail),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        } else {
            val pagerState = rememberPagerState(pageCount = { imageUrls.size })

            Box(modifier = Modifier.fillMaxWidth()) {
                HorizontalPager(
                    state = pagerState,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f),
                    key = { index -> index },
                ) { page ->
                    SubcomposeAsyncImage(
                        model = imageUrls[page],
                        contentDescription = stringResource(R.string.desc_invitation_image),
                        contentScale = ContentScale.Crop,
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .clickable { onImageClick(page) },
                        loading = {
                            Box(
                                modifier =
                                    Modifier
                                        .fillMaxSize()
                                        .background(NachoTheme.colorScheme.backgroundSecondary),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator(
                                    color = NachoTheme.colorScheme.brandPrimary,
                                )
                            }
                        },
                        success = {
                            SubcomposeAsyncImageContent()
                        },
                        error = {
                            Box(
                                modifier =
                                    Modifier
                                        .fillMaxSize()
                                        .background(NachoTheme.colorScheme.backgroundSecondary),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.ic_error_outline_24),
                                    contentDescription = null,
                                    tint = NachoTheme.colorScheme.textTertiary,
                                )
                            }
                        },
                    )
                }

                if (imageUrls.size >= 2) {
                    MediaOverlay(
                        text = "${pagerState.currentPage + 1}/${imageUrls.size}",
                        shape = NachoTheme.shapes.medium,
                        modifier =
                            Modifier
                                .align(Alignment.TopEnd)
                                .padding(
                                    top = NachoSpacing.medium,
                                    end = NachoSpacing.medium,
                                ),
                    )
                }
            }
        }
    }
}

@Composable
@PreviewTheme
private fun ImageSectionPreview() {
    NachoTheme {
        ImageSection(
            imageUrls =
                listOf(
                    "https://example.com/image.jpg",
                    "https://example.com/image.jpg",
                    "https://example.com/image.jpg",
                ).toImmutableList(),
            onImageClick = { },
        )
    }
}
