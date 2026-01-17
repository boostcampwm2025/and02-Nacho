package com.andlife.ui.component.loading

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme

@Composable
fun InvitationLoadingIndicator(
    modifier: Modifier = Modifier,
    text: String? = null,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(NachoSpacing.medium),
        ) {
            CircularProgressIndicator(
                color = NachoTheme.colorScheme.brandPrimary,
            )
            text?.let {
                Text(
                    text = it,
                    style = NachoTheme.typography.bodyMediumRegular,
                    color = NachoTheme.colorScheme.textSecondary,
                )
            }
        }
    }
}

@PreviewTheme
@Composable
private fun InvitationLoadingIndicatorPreview() {
    NachoTheme {
        InvitationLoadingIndicator()
    }
}
