package com.andlife.ui.section.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.model.invitation.DateTimeInfo
import com.andlife.ui.R
import com.andlife.ui.util.toDisplayDateString
import com.andlife.ui.util.toDisplayTimeString

@Composable
fun DateSection(
    dateTime: DateTimeInfo,
    modifier: Modifier = Modifier,
) {

    val dateText = dateTime.date.toDisplayDateString()
    val timeText = dateTime.startTime.toDisplayTimeString()

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(NachoTheme.colorScheme.backgroundPrimary)
                .padding(vertical = NachoSpacing.xSmall, horizontal = NachoSpacing.large),
    ) {
        IconTextRow(
            iconRes = R.drawable.ic_calendar_24,
            date = dateText,
            time = timeText,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun IconTextRow(
    iconRes: Int,
    date: String,
    time: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(NachoSpacing.xSmall),
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = Color.Unspecified,
        )

        Column(
            modifier = Modifier.padding(NachoSpacing.small),
            verticalArrangement = Arrangement.spacedBy(NachoSpacing.xSmall),
        ) {
            Text(
                text = date,
                style = NachoTheme.typography.bodyLargeRegular,
                color = NachoTheme.colorScheme.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = time,
                style = NachoTheme.typography.bodyMediumRegular,
                color = NachoTheme.colorScheme.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun EmptyDateSection(
    datePlaceholder: String = stringResource(R.string.txt_datetime_placeholder),
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(NachoTheme.colorScheme.backgroundPrimary)
            .padding(vertical = NachoSpacing.xSmall, horizontal = NachoSpacing.large),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(NachoSpacing.medium),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_calendar_24),
                contentDescription = stringResource(R.string.desc_date_time),
                tint = NachoTheme.colorScheme.textTertiary,
            )
            Text(
                text = datePlaceholder,
                style = NachoTheme.typography.bodyLargeMedium,
                color = NachoTheme.colorScheme.textTertiary,
                modifier = Modifier.alpha(0.6f),
            )
        }
    }
}

@PreviewTheme
@Composable
private fun DateSectionPreview() {
    NachoTheme {
        DateSection(
            dateTime = DateTimeInfo()
        )
    }
}

@PreviewTheme
@Composable
private fun EmptyDateSectionPreview() {
    NachoTheme {
        EmptyDateSection()
    }
}
