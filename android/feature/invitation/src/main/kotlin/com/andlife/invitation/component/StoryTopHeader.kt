package com.andlife.invitation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import coil3.compose.AsyncImage
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.InvitationIconSize
import com.andlife.designsystem.theme.InvitationSpacing
import com.andlife.designsystem.theme.InvitationTheme
import com.andlife.invitation.util.toDateTimeFormat
import com.andlife.ui.R
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun StoryTopHeader(
    name: String,
    date: LocalDateTime,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    profileUrl: String? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onClose) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back_24),
                contentDescription = "Back",
                tint = InvitationTheme.colorScheme.iconTertiary,
            )
        }

        Box(
            modifier =
                Modifier
                    .size(InvitationIconSize.large)
                    .clip(CircleShape)
                    .background(InvitationTheme.colorScheme.backgroundSecondary),
            contentAlignment = Alignment.Center,
        ) {
            if (!profileUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = profileUrl,
                    contentDescription = stringResource(R.string.desc_user_profile),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.ic_person_24),
                    contentDescription = stringResource(R.string.desc_btn_back),
                    tint = InvitationTheme.colorScheme.iconSecondary,
                    modifier = Modifier.size(InvitationIconSize.medium),
                )
            }
        }

        Spacer(modifier = Modifier.width(InvitationSpacing.medium))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                color = InvitationTheme.colorScheme.textOnPrimary,
                style = InvitationTheme.typography.bodyMediumMedium,
            )
            Text(
                text = date.toDateTimeFormat(),
                color = InvitationTheme.colorScheme.textTertiary,
                style = InvitationTheme.typography.bodySmallRegular,
            )
        }

        IconButton(onClick = { /* Todo: 다운로드 로직 */ }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_download_24),
                contentDescription = stringResource(R.string.desc_btn_download),
                tint = InvitationTheme.colorScheme.iconTertiary,
            )
        }
    }
}

@PreviewTheme
@Composable
private fun StoryTopHeaderPreview() {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    InvitationTheme {
        StoryTopHeader(
            name = "사용자 이름",
            date = now,
            onClose = {},
        )
    }
}
