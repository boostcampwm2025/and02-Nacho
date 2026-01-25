package com.andlife.ui.component.listitem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import com.andlife.designsystem.component.NachoDdayChip
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoIconSize
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoStroke
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.ui.R
import com.andlife.ui.component.loading.InvitationLoadingIndicator

@Composable
fun InvitationListItem(
    imageUrl: String,
    title: String,
    startTime: String,
    hostName: String?,
    address: String,
    dDayText: String?,
    onClick: () -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        shape = NachoTheme.shapes.medium,
        colors =
            CardDefaults.cardColors(
                containerColor = NachoTheme.colorScheme.backgroundPrimary,
            ),
        border =
            BorderStroke(
                NachoStroke.small,
                NachoTheme.colorScheme.backgroundBorder,
            ),
        modifier = modifier,
    ) {
        Column {
            Box {
                if (imageUrl.isBlank()) {
                    Image(
                        painter = painterResource(R.drawable.bg_thumbnail),
                        contentDescription = stringResource(R.string.desc_invitation_list_image),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f),
                    )
                } else {
                    SubcomposeAsyncImage(
                        model = imageUrl,
                        contentDescription = stringResource(R.string.desc_invitation_list_image),
                        contentScale = ContentScale.Crop,
                        loading = { InvitationLoadingIndicator() },
                        success = {
                            SubcomposeAsyncImageContent()
                        },
                        error = {
                            Image(
                                painter = painterResource(R.drawable.bg_thumbnail),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f),
                    )
                }

                IconButton(
                    onClick = onMoreClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(NachoSpacing.medium)
                        .background(
                            color = NachoTheme.colorScheme.backgroundPrimary,
                            shape = NachoTheme.shapes.extraLarge,
                        )
                        .size(NachoIconSize.large),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_more_vert_24),
                        contentDescription = stringResource(R.string.desc_invitation_more_btn),
                        modifier = Modifier.size(NachoIconSize.small)
                    )
                }
            }

            Column(
                modifier = Modifier.padding(NachoSpacing.medium),
                verticalArrangement = Arrangement.spacedBy(NachoSpacing.medium),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = title,
                        style = NachoTheme.typography.headingSmallSemiBold,
                        color = NachoTheme.colorScheme.textPrimary,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    dDayText?.let {
                        NachoDdayChip(
                            label = it
                        )
                    }
                }

                hostName?.let {
                    Text(
                        text = it,
                        style = NachoTheme.typography.bodyMediumMedium,
                        color = NachoTheme.colorScheme.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(NachoSpacing.xSmall),
                ) {
                    IconTextRow(iconRes = R.drawable.ic_calendar_24, text = startTime)
                    IconTextRow(iconRes = R.drawable.ic_location_24, text = address)
                }
            }
        }
    }
}

@Composable
private fun IconTextRow(
    iconRes: Int,
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(NachoSpacing.xSmall),
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = stringResource(R.string.desc_invitation_icon),
            tint = Color.Unspecified,
        )

        Text(
            text = text,
            style = NachoTheme.typography.bodyMediumRegular,
            color = NachoTheme.colorScheme.textSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@PreviewTheme
@Composable
private fun InvitationListItemPreview() {
    NachoTheme {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(NachoSpacing.small),
        ) {
            InvitationListItem(
                imageUrl = "https://example.com/image.jpg",
                title = "네부캠 송년회",
                startTime = "2025년 1월 15일 오후 1시",
                hostName = "안드라이프",
                address = "강남대로62길 23 4층 코드스쿼드",
                dDayText = "D-3",
                onClick = {},
                onMoreClick = {},
            )

            InvitationListItem(
                imageUrl = "https://example.com/image.jpg",
                title = "네부캠 송년회",
                startTime = "2025년 1월 15일 오후 1시",
                hostName = null,
                address = "강남대로62길 23 4층 코드스쿼드",
                dDayText = "D-3",
                onClick = {},
                onMoreClick = {},
            )
        }
    }
}
