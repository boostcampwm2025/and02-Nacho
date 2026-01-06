package com.andlife.invitation.screen

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun InvitationDetailRoute(
    id: Long,
    modifier: Modifier = Modifier,
) {
    Log.d("InvitationDetail", "InvitationDetailRoute 진입! id=$id")

    InvitationDetailScreen(
        id = id,
        modifier = modifier,
    )
}

@Composable
private fun InvitationDetailScreen(
    id: Long,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Text(text = "InvitationDetailScreen")
            Text(
                text = "받은 ID: $id",
            )
        }
    }
}
