package com.andlife.myinvitation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.andlife.designsystem.component.NachoButton

@Composable
fun MyInvitationRoute(
    onNavigateToCreate: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    MyInvitationScreen(
        onNavigateToCreate = onNavigateToCreate,
        onNavigateToDetail = onNavigateToDetail,
        modifier = modifier,
    )
}

@Composable
private fun MyInvitationScreen(
    onNavigateToCreate: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
    ) { padding ->
        // TODO: 임시 나의 초대 화면
        Column(modifier = Modifier.padding(padding)) {
            Text(text = "MyInvitationScreen")
            NachoButton(
                onClick = onNavigateToCreate,
            ) {
                Text("초대 생성")
            }
            NachoButton(
                onClick = { onNavigateToDetail(123L) }, // TODO: 실제 초대장 id 전달 필요
            ) {
                Text("임시 초대장 123")
            }
            NachoButton(
                onClick = { onNavigateToDetail(456L) }, // TODO: 실제 초대장 id 전달 필요
            ) {
                Text("임시 초대장 456")
            }
        }
    }
}
