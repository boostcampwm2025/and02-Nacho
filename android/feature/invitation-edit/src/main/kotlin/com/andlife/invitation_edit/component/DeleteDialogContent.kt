package com.andlife.invitation_edit.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.andlife.designsystem.component.NachoButton
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.invitation_edit.R

@Composable
internal fun DeleteDialogContent(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(NachoSpacing.large),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.txt_remove_announcement),
            style = NachoTheme.typography.bodyLargeSemiBold
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = NachoSpacing.large),
            horizontalArrangement = Arrangement.spacedBy(NachoSpacing.medium)
        ) {
            NachoButton(
                modifier = Modifier.weight(1f),
                onClick = onDismiss,
                containerColor = NachoTheme.colorScheme.backgroundBorder,
            ) {
                Text(
                    text = stringResource(R.string.txt_cancel),
                    style = NachoTheme.typography.bodyMediumMedium
                )
            }

            NachoButton(
                modifier = Modifier.weight(1f),
                onClick = onConfirm
            ) {
                Text(
                    text = stringResource(R.string.txt_remove),
                    style = NachoTheme.typography.bodyMediumMedium
                )
            }

        }
    }
}
