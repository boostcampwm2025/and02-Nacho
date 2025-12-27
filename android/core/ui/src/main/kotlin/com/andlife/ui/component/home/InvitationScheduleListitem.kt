package com.andlife.ui.component.home

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import com.andlife.designsystem.component.InvitationDdayChip
import com.andlife.designsystem.theme.InvitationSpacing
import com.andlife.designsystem.theme.InvitationStroke
import com.andlife.designsystem.theme.InvitationTheme

@Composable
fun InvitationScheduleListitem(
    imageUrl: String,
    title: String,
    dateTime: String,
    hostName: String,
    ddayText: String,
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
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
            )

            Column(
                modifier = Modifier.padding(InvitationSpacing.medium),
                verticalArrangement = Arrangement.spacedBy(InvitationSpacing.small)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = InvitationTheme.typography.bodyLarge1,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    InvitationDdayChip(
                        label = ddayText
                    )
                }

                Text(
                    text = dateTime,
                    style = InvitationTheme.typography.bodyMedium2,
                    color = InvitationTheme.colorScheme.textSecondary
                )

                Text(
                    text = hostName,
                    style = InvitationTheme.typography.bodyMedium2,
                    color = InvitationTheme.colorScheme.textTertiary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InvitationScheduleListitemPreview(){
    InvitationTheme {
        InvitationScheduleListitem(
            imageUrl = "https://example.com/image.jpg",
            title = "네부캠 송년회",
            dateTime = "2025년 1월 15일 오후 1시",
            hostName = "안드라이프",
            ddayText = "D-3",
            onClick = {},
        )
    }
}