package com.andlife.invitation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andlife.designsystem.component.NachoButton
import com.andlife.invitation.model.InvitationUiState
import com.andlife.invitation.viewmodel.InvitationViewModel

@Composable
fun InvitationRoute(
    onNavigateToDetail: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InvitationViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    InvitationScreen(
        onNavigateToDetail = onNavigateToDetail,
        uiState = uiState,
        modifier = modifier,
    )
}

@Composable
private fun InvitationScreen(
    uiState: InvitationUiState,
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

        Text("전달 받은 초대장 리스트 : ${uiState.invitationIds}")
    }
}
