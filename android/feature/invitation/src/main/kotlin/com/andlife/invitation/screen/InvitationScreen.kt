package com.andlife.invitation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.andlife.designsystem.component.NachoButton

@Composable
fun InvitationRoute(
    onInvitationClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    InvitationScreen(
        modifier = modifier,
        onInvitationClick = onInvitationClick,
    )
}

@Composable
fun InvitationScreen(
    modifier: Modifier = Modifier,
    onInvitationClick: (Long) -> Unit,
) {
    Column(
        modifier = modifier,
    ) {
        NachoButton(
            // 임시로 ID 1번 전달
            onClick = { onInvitationClick(1L) },
        ) {
            Text("초대장으로 이동")
        }
    }
}
