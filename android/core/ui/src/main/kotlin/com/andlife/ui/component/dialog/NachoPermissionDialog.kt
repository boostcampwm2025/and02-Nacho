package com.andlife.ui.component.dialog

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import com.andlife.designsystem.component.dialog.NachoDialog
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.ui.R

@Composable
fun NachoPermissionDialog(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    NachoDialog(
        onDismiss = onDismiss,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(NachoSpacing.xLarge),
        ) {
            Text(
                text = message,
                style = NachoTheme.typography.bodyMediumMedium,
                color = NachoTheme.colorScheme.textSecondary,
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = NachoSpacing.xLarge),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(onClick = onDismiss) {
                    Text(
                        text = stringResource(R.string.btn_label_cancel),
                        color = NachoTheme.colorScheme.textSecondary,
                    )
                }

                Spacer(modifier = Modifier.padding(horizontal = NachoSpacing.small))

                TextButton(
                    onClick = {
                        onDismiss()
                        context.openAppSettings()
                    },
                    shape = NachoTheme.shapes.small,
                ) {
                    Text(
                        text = stringResource(R.string.btn_label_to_setting),
                        color = NachoTheme.colorScheme.brandPrimary,
                    )
                }
            }
        }
    }
}

private fun Context.openAppSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    intent.data = "package:$packageName".toUri()
    startActivity(intent)
}
