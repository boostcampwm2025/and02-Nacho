package com.andlife.ui.component.invitation.collection

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
import com.andlife.designsystem.theme.NachoIconSize
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.ui.util.toDateTimeFormat
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import com.andlife.ui.R
import com.andlife.designsystem.R as designR

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
                tint = NachoTheme.colorScheme.iconTertiary,
            )
        }

        Box(
            modifier =
                Modifier
                    .size(NachoIconSize.large)
                    .clip(CircleShape)
                    .background(NachoTheme.colorScheme.backgroundSecondary),
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
                    painter = painterResource(designR.drawable.ic_person_24),
                    contentDescription = stringResource(R.string.desc_btn_back),
                    tint = NachoTheme.colorScheme.iconSecondary,
                    modifier = Modifier.size(NachoIconSize.medium),
                )
            }
        }

        Spacer(modifier = Modifier.width(NachoSpacing.medium))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                color = NachoTheme.colorScheme.textOnPrimary,
                style = NachoTheme.typography.bodyMediumMedium,
            )
            Text(
                text = date.toDateTimeFormat(),
                color = NachoTheme.colorScheme.textTertiary,
                style = NachoTheme.typography.bodySmallRegular,
            )
        }

        IconButton(onClick = { /* Todo: 다운로드 로직 */ }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_download_24),
                contentDescription = stringResource(R.string.desc_btn_download),
                tint = NachoTheme.colorScheme.iconTertiary,
            )
        }
    }
}

@PreviewTheme
@Composable
private fun StoryTopHeaderPreview() {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    NachoTheme {
        StoryTopHeader(
            name = "사용자 이름",
            date = now,
            onClose = {},
        )
    }
}
