package com.andlife.ui.component.listitem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
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
import coil3.compose.AsyncImage
import com.andlife.designsystem.component.NachoDdayChip
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoIconSize
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoStroke
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.ui.R

@Composable
fun InvitationListItem(
    imageUrl: String,
    title: String,
    startTime: String,
    hostName: String?,
    address: String,
    dDayText: String,
    onClick: () -> Unit,
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
                AsyncImage(
                    model = imageUrl,
                    contentDescription = stringResource(R.string.desc_invitation_list_image),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.ic_placeholder_default_24),
                    error = painterResource(R.drawable.ic_error_outline_24),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f),
                )

                IconButton(
                    onClick = { },
                    modifier =
                        Modifier
                            .align(Alignment.TopEnd)
                            .padding(NachoSpacing.large)
                            .background(
                                color = NachoTheme.colorScheme.backgroundPrimary,
                                shape = NachoTheme.shapes.extraLarge,
                            ).size(NachoIconSize.large),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_more_vert_24),
                        contentDescription = stringResource(R.string.desc_invitation_more_btn),
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

                    NachoDdayChip(
                        label = dDayText,
                    )
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
private fun NachoListItemPreview() {
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
            )

            InvitationListItem(
                imageUrl = "https://example.com/image.jpg",
                title = "네부캠 송년회",
                startTime = "2025년 1월 15일 오후 1시",
                hostName = null,
                address = "강남대로62길 23 4층 코드스쿼드",
                dDayText = "D-3",
                onClick = {},
            )
        }
    }
}
