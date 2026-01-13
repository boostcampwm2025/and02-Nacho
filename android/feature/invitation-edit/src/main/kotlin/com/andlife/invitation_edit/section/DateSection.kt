package com.andlife.invitation_edit.section

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.andlife.designsystem.component.NachoTextField
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.InvitationTheme
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.invitation_edit.R
import com.andlife.invitation_edit.component.FormLabel
import kotlinx.datetime.LocalDate

@Composable
internal fun DateSection(
    date: LocalDate?,
    onDateClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dateString =
        remember(date) {
            if (date != null) {
                "${date.year}-${date.monthNumber}-${date.dayOfMonth}"
            } else {
                ""
            }
        }
    Box(modifier = modifier.background(NachoTheme.colorScheme.backgroundPrimary)) {
        Column(
            modifier = Modifier.padding(NachoSpacing.large),
            verticalArrangement = Arrangement.spacedBy(NachoSpacing.small),
        ) {
            FormLabel(title = stringResource(R.string.txt_invitation_date))
            NachoTextField(
                value = dateString,
                onValueChange = {},
                placeholder = stringResource(R.string.desc_invitation_date),
                enabled = false,
                readOnly = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = stringResource(R.string.desc_invitation_date),
                    )
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable { onDateClick() },
            )
        }
    }
}

@Composable
@PreviewTheme
private fun DateSectionPreview() {
    InvitationTheme {
        DateSection(
            date = LocalDate(2026, 1, 6),
            onDateClick = {},
        )
    }
}
