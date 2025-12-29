package com.andlife.ui.component.loading

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.InvitationSpacing
import com.andlife.designsystem.theme.InvitationTheme
import com.andlife.ui.R

@Composable
fun InvitationLoadingError(onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.label_loading_fetch_failed),
                color = InvitationTheme.colorScheme.textTertiary,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(InvitationSpacing.medium))
            Button(
                onClick = onRetry,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = InvitationTheme.colorScheme.backgroundBorder,
                        contentColor = InvitationTheme.colorScheme.textPrimary,
                    ),
            ) {
                Text(text = stringResource(R.string.label_loading_retry))
            }
        }
    }
}

@PreviewTheme
@Composable
private fun InvitationLoadingErrorPreview() {
    InvitationTheme {
        InvitationLoadingError(onRetry = {})
    }
}
