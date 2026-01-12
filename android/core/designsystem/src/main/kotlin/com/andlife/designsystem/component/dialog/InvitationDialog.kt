package com.andlife.designsystem.component.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.InvitationSpacing
import com.andlife.designsystem.theme.InvitationTheme

@Composable
fun InvitationDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = InvitationSpacing.large),
            shape = InvitationTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = InvitationTheme.colorScheme.backgroundTertiary,
                contentColor = InvitationTheme.colorScheme.textPrimary
            )
        ) {
            content()
        }
    }
}

@Composable
@PreviewTheme
private fun InvitationDialogPreview() {
    InvitationTheme {
        var dialog by remember { mutableStateOf(false) }
        Column(modifier = Modifier.fillMaxSize()) {
            Button(onClick = { dialog = true }) {
                Text(text = "Dialog")
            }
        }
        if (dialog) {
            InvitationDialog(onDismiss = { dialog = false }) {
                Text(text = "Dialog")
            }
        }
    }
}
