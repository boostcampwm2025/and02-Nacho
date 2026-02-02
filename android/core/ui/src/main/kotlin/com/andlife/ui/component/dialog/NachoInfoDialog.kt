package com.andlife.ui.component.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.andlife.designsystem.component.dialog.NachoDialog
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.ui.R

@Composable
fun NachoInfoDialog(
    title: String,
    message: String,
    confirmText: String,
    dismissText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    showDoNotShowAgain: Boolean = false,
    onDoNotShowAgainChecked: () -> Unit = {},
) {
    var checkedState by remember { mutableStateOf(false) }

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
                text = title,
                style = NachoTheme.typography.headingSmallSemiBold,
                color = NachoTheme.colorScheme.textPrimary,
            )

            Text(
                text = message,
                modifier = Modifier.padding(top = NachoSpacing.medium, bottom = NachoSpacing.xLarge),
                style = NachoTheme.typography.bodyMediumMedium,
                color = NachoTheme.colorScheme.textSecondary,
            )

            if (showDoNotShowAgain) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = NachoSpacing.medium)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { checkedState = !checkedState },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                ) {
                    Checkbox(
                        checked = checkedState,
                        onCheckedChange = { checkedState = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = NachoTheme.colorScheme.brandPrimary,
                        ),
                    )
                    Text(
                        text = stringResource(R.string.dialog_do_not_show_again),
                        style = NachoTheme.typography.bodySmallMedium,
                        color = NachoTheme.colorScheme.textSecondary,
                        modifier = Modifier.padding(start = NachoSpacing.twoXSmall),
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(onClick = onDismiss) {
                    Text(
                        text = dismissText,
                        color = NachoTheme.colorScheme.textSecondary,
                        style = NachoTheme.typography.bodyMediumSemiBold,
                    )
                }

                Spacer(modifier = Modifier.padding(horizontal = NachoSpacing.small))

                TextButton(
                    onClick = {
                        if (checkedState) onDoNotShowAgainChecked()
                        onConfirm()
                    },
                    shape = NachoTheme.shapes.small,
                ) {
                    Text(
                        text = confirmText,
                        color = NachoTheme.colorScheme.brandPrimary,
                        style = NachoTheme.typography.bodyMediumSemiBold,
                    )
                }
            }
        }
    }
}
