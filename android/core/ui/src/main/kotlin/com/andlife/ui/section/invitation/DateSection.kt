package com.andlife.ui.section.invitation

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.andlife.ui.R
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.model.invitation.InvitationTimeUiModel
import kotlinx.datetime.LocalDate

@Composable
fun DateSection(
    date: LocalDate?,
    startTime: InvitationTimeUiModel?,
    modifier: Modifier = Modifier,
) {

    val formattedDate = date?.let {
        "${it.year}${stringResource(R.string.txt_year)} " +
            "${it.monthNumber}${stringResource(R.string.txt_month)} " +
            "${it.dayOfMonth}${stringResource(R.string.txt_day)}"
    } ?: ""

    val formattedTime = formatInvitationTime(startTime)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(NachoTheme.colorScheme.backgroundPrimary)
            .padding(vertical = NachoSpacing.xSmall, horizontal = NachoSpacing.large),
    ) {
        date?.let {
            IconTextRow(
                iconRes = R.drawable.ic_calendar_24,
                date = formattedDate,
                time = formattedTime,
            )
        }
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
            modifier = modifier.padding(NachoSpacing.small),
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
private fun formatInvitationTime(time: InvitationTimeUiModel?): String {
    if (time == null) return ""

    val amPmArray = stringArrayResource(R.array.array_am_pm)
    val hourSuffix = stringResource(R.string.txt_hour)
    val minSuffix = stringResource(R.string.txt_min)

    val period = if (time.hour < 12) amPmArray[0] else amPmArray[1]

    val displayHour = when {
        time.hour == 0 -> 12
        time.hour > 12 -> time.hour - 12
        else -> time.hour
    }

    val minuteSuffix = if (time.min == 0) "" else " ${time.min}$minSuffix"

    return "$period $displayHour$hourSuffix$minuteSuffix"
}

@PreviewTheme
@Composable
private fun DateSectionPreview() {
    NachoTheme {
        DateSection(
            date = LocalDate(2026, 1, 24),
            startTime = InvitationTimeUiModel(12, 0),
        )
    }
}
