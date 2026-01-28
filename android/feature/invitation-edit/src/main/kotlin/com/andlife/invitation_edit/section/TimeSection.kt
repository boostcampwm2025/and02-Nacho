package com.andlife.invitation_edit.section

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import com.andlife.designsystem.component.NachoTextField
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.invitation_edit.R
import com.andlife.invitation_edit.component.FormLabel
import com.andlife.invitation_edit.model.form.InvitationTimeUiModel

@SuppressLint("DefaultLocale")
@Composable
internal fun TimeSection(
    startTime: InvitationTimeUiModel?,
    endTime: InvitationTimeUiModel?,
    onStartTimeClick: () -> Unit,
    onEndTimeClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
) {
    val res = LocalResources.current
    val startTimeString =
        remember(startTime, res) {
            if (startTime != null) {
                res.getString(R.string.txt_time_format, startTime.hour, startTime.min)
            } else {
                ""
            }
        }

    val endTimeString =
        remember(endTime, res) {
            if (endTime != null) {
                res.getString(R.string.txt_time_format, endTime.hour, endTime.min)
            } else {
                ""
            }
        }

    Box(modifier = modifier.background(NachoTheme.colorScheme.backgroundPrimary)) {
        Column(
            modifier = Modifier.padding(NachoSpacing.large),
            verticalArrangement = Arrangement.spacedBy(NachoSpacing.small),
        ) {
            FormLabel(title = stringResource(R.string.txt_invitation_time))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NachoTextField(
                    value = startTimeString,
                    onValueChange = {},
                    placeholder = stringResource(R.string.desc_start_time),
                    enabled = false,
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = stringResource(R.string.desc_start_time),
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            enabled = !isLoading,
                            onClick = onStartTimeClick
                        )
                )
                Text(
                    text = "~",
                    color = NachoTheme.colorScheme.textSecondary,
                    modifier = Modifier.padding(horizontal = NachoSpacing.medium),
                )
                NachoTextField(
                    value = endTimeString,
                    onValueChange = {},
                    placeholder = stringResource(R.string.desc_end_time),
                    enabled = false,
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = stringResource(R.string.desc_end_time),
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            enabled = !isLoading,
                            onClick = onEndTimeClick
                        ),
                )
            }
        }
    }
}

@Composable
@PreviewTheme
private fun TimeSectionPreview() {
    NachoTheme {
        TimeSection(
            startTime = InvitationTimeUiModel(10, 30),
            endTime = InvitationTimeUiModel(11, 30),
            onStartTimeClick = {},
            onEndTimeClick = {},
        )
    }
}
