package com.andlife.ui.component.invitation

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
import androidx.compose.ui.text.style.TextOverflow
import coil3.compose.AsyncImage
import com.andlife.designsystem.component.InvitationDdayChip
import com.andlife.designsystem.preview.ThemePreview
import com.andlife.designsystem.theme.InvitationIconSize
import com.andlife.designsystem.theme.InvitationSpacing
import com.andlife.designsystem.theme.InvitationStroke
import com.andlife.designsystem.theme.InvitationTheme
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
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = InvitationTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = InvitationTheme.colorScheme.backgroundPrimary
        ),
        border = BorderStroke(
            InvitationStroke.small,
            InvitationTheme.colorScheme.backgroundBorder
        ),
        modifier = modifier,
    ) {
        Column {
            Box {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.ic_placeholder_default_24),
                    error = painterResource(R.drawable.ic_error_outline_24),
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                )

                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(InvitationSpacing.large)
                        .background(
                            color = InvitationTheme.colorScheme.backgroundPrimary,
                            shape = InvitationTheme.shapes.extraLarge
                        )
                        .size(InvitationIconSize.large)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_more_vert_24),
                        contentDescription = null,
                    )
                }
            }

            Column(
                modifier = Modifier.padding(InvitationSpacing.medium),
                verticalArrangement = Arrangement.spacedBy(InvitationSpacing.medium)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = InvitationTheme.typography.headingSmallSemiBold,
                        color = InvitationTheme.colorScheme.textPrimary,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    InvitationDdayChip(
                        label = dDayText
                    )
                }

                hostName?.let {
                    Text(
                        text = it,
                        style = InvitationTheme.typography.bodyMediumMedium,
                        color = InvitationTheme.colorScheme.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(InvitationSpacing.xSmall)
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
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(InvitationSpacing.xSmall)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = Color.Unspecified
        )

        Text(
            text = text,
            style = InvitationTheme.typography.bodyMediumRegular,
            color = InvitationTheme.colorScheme.textSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@ThemePreview
@Composable
private fun InvitationListItemPreview(){
    InvitationTheme {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(InvitationSpacing.small)
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