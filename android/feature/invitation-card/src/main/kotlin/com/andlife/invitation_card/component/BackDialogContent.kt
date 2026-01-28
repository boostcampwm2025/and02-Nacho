package com.andlife.invitation_card.component

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
import com.andlife.invitation_card.R

@Composable
internal fun BackDialogContent(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = NachoSpacing.xLarge, bottom = NachoSpacing.large)
            .padding(horizontal = NachoSpacing.xLarge),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = stringResource(R.string.txt_exit_dialog_title),
            style = NachoTheme.typography.headingSmallSemiBold,
            color = NachoTheme.colorScheme.textPrimary
        )

        Text(
            modifier = Modifier.padding(top = NachoSpacing.small),
            text = stringResource(R.string.txt_exit_dialog_message),
            style = NachoTheme.typography.bodyMediumRegular,
            color = NachoTheme.colorScheme.textSecondary,
            lineHeight = NachoTheme.typography.bodyMediumRegular.lineHeight * 1.3
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = NachoSpacing.twoXLarge),
            horizontalArrangement = Arrangement.spacedBy(NachoSpacing.small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NachoButton(
                modifier = Modifier.weight(1f),
                onClick = onConfirm,
                containerColor = NachoTheme.colorScheme.backgroundSecondary,
                contentColor = NachoTheme.colorScheme.textSecondary
            ) {
                Text(
                    text = stringResource(R.string.btn_exit),
                    style = NachoTheme.typography.bodyMediumSemiBold
                )
            }

            NachoButton(
                modifier = Modifier.weight(1f),
                onClick = onDismiss,
                containerColor = NachoTheme.colorScheme.brandPrimary,
                contentColor = NachoTheme.colorScheme.backgroundPrimary
            ) {
                Text(
                    text = stringResource(R.string.btn_continue),
                    style = NachoTheme.typography.bodyMediumSemiBold
                )
            }
        }
    }
}
