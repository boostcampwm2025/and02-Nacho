package com.andlife.invitation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun InvitationDetailRoute(
    modifier: Modifier = Modifier,
) {
    InvitationDetailScreen(
        modifier = modifier,
    )
}

@Composable
private fun InvitationDetailScreen(
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Text(text = "InvitationDetailScreen")
        }
    }
}
