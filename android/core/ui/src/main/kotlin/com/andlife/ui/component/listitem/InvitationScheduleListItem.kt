package com.andlife.ui.component.listitem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import coil3.compose.AsyncImage
import com.andlife.designsystem.component.NachoDdayChip
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoStroke
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.ui.R

@Composable
fun InvitationScheduleListItem(
    imageUrl: String,
    title: String,
    startTime: String,
    hostName: String,
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
            AsyncImage(
                model = imageUrl,
                contentDescription = stringResource(R.string.desc_schedule_list_image),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.ic_placeholder_default_24),
                error = painterResource(R.drawable.ic_error_outline_24),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f),
            )

            Column(
                modifier = Modifier.padding(NachoSpacing.medium),
                verticalArrangement = Arrangement.spacedBy(NachoSpacing.small),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = title,
                        style = NachoTheme.typography.bodyLargeSemiBold,
                        color = NachoTheme.colorScheme.textPrimary,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    NachoDdayChip(
                        label = dDayText,
                    )
                }

                Text(
                    text = startTime,
                    style = NachoTheme.typography.bodyMediumRegular,
                    color = NachoTheme.colorScheme.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = hostName,
                    style = NachoTheme.typography.bodyMediumRegular,
                    color = NachoTheme.colorScheme.textTertiary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@PreviewTheme
@Composable
private fun InvitationScheduleListItemPreview() {
    NachoTheme {
        InvitationScheduleListItem(
            imageUrl = "https://example.com/image.jpg",
            title = "네부캠 송년회",
            startTime = "2025년 1월 15일 오후 1시",
            hostName = "안드라이프",
            dDayText = "D-3",
            onClick = {},
        )
    }
}
