package com.andlife.invitation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.andlife.designsystem.component.NachoButton

@Composable
fun InvitationRoute(
    onNavigateToDetail: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    InvitationScreen(
        onNavigateToDetail = onNavigateToDetail,
        modifier = modifier,
    )
}

@Composable
private fun InvitationScreen(
    modifier: Modifier = Modifier,
    onNavigateToDetail: (Long) -> Unit,
) {
    // TODO: 초대장 리스트 화면
    // - 로딩 상태 처리 필요 (uiState.isLoading)
    // - 에러 상태 처리 필요
    // - 빈 상태 처리 필요 (초대장이 없을 때)
    // - Scaffold, TopAppBar 등 추가 필요
    Column(
        modifier = modifier,
    ) {
        NachoButton(
            // 임시로 ID 1번 전달
            onClick = { onNavigateToDetail(1L) },
        ) {
            Text("초대장으로 이동")
        }
    }
}
