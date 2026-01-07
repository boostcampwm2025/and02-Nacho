package com.andlife.invitation.screen.detail

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

private const val TAG = "InvitationDetailScreen"

@Composable
fun InvitationDetailRoute(
    id: Long,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Log.d(TAG, "전달 받은 ID: $id")
}

@Composable
private fun InvitationDetailScreen() {

}
