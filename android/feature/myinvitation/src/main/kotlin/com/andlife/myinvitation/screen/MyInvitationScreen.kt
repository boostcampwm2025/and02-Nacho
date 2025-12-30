package com.andlife.myinvitation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.andlife.designsystem.component.InvitationButton

@Composable
fun MyInvitationRoute(
    onNavigateToDetail: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MyInvitationScreen(
        onNavigateToDetail = onNavigateToDetail,
        modifier = modifier
    )
}

@Composable
private fun MyInvitationScreen(
    onNavigateToDetail: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Text(text = "MyInvitationScreen")
            InvitationButton(
                onClick = onNavigateToDetail,
            ) {
                Text("리스트 카드 선택")
            }
        }
    }
}
