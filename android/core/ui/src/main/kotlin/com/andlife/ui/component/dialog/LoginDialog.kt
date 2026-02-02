package com.andlife.ui.component.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.andlife.designsystem.component.dialog.NachoDialog
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.ui.R

@Composable
fun LoginDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    NachoDialog(onDismiss = onDismiss) {
        Column(
            modifier = Modifier.padding(NachoSpacing.xLarge),
            verticalArrangement = Arrangement.spacedBy(NachoSpacing.medium)
        ) {
            Text(
                text = stringResource(R.string.txt_label_login),
                color = NachoTheme.colorScheme.textPrimary,
                style = NachoTheme.typography.headingSmallSemiBold,
            )
            Text(
                text = stringResource(R.string.msg_guide_login),
                color = NachoTheme.colorScheme.textSecondary,
                style = NachoTheme.typography.bodyMediumRegular,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(onClick = onDismiss) {
                    Text(
                        text = stringResource(R.string.txt_label_cancel),
                        color = NachoTheme.colorScheme.textPrimary,
                        style = NachoTheme.typography.bodyMediumSemiBold,
                    )
                }
                TextButton(onClick = onConfirm) {
                    Text(
                        text = stringResource(R.string.txt_move),
                        color = NachoTheme.colorScheme.brandDark,
                        style = NachoTheme.typography.bodyMediumSemiBold,
                    )
                }
            }
        }
    }
}

@PreviewTheme
@Composable
private fun LoginDialogPreview() {
    NachoTheme {
        LoginDialog(
            onDismiss = {},
            onConfirm = {}
        )
    }
}
