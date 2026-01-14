package com.andlife.ui.section.invitation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import coil3.compose.AsyncImage
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoIconSize
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.ui.R
import com.andlife.designsystem.R as designR

@Composable
fun AuthorSection(
    profileUrl: String,
    author: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(NachoTheme.colorScheme.backgroundPrimary)
                .padding(NachoSpacing.medium),
        verticalArrangement = Arrangement.spacedBy(NachoSpacing.small),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(NachoSpacing.small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = profileUrl,
                contentDescription = stringResource(R.string.desc_profile_image),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(designR.drawable.ic_person_24),
                error = painterResource(designR.drawable.ic_person_24),
                modifier =
                    Modifier
                        .size(NachoIconSize.xLarge)
                        .clip(CircleShape),
            )

            Text(
                text = author,
                style = NachoTheme.typography.bodyLargeMedium,
                color = NachoTheme.colorScheme.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false),
            )

            Text(
                text = stringResource(R.string.txt_host),
                style = NachoTheme.typography.bodyMediumRegular,
                color = NachoTheme.colorScheme.textTertiary,
            )
        }
    }
}

@PreviewTheme
@Composable
private fun AuthorSectionPreview() {
    NachoTheme {
        AuthorSection(
            profileUrl = "https://example.com/image.jpg",
            author = "안드라이프",
        )
    }
}
