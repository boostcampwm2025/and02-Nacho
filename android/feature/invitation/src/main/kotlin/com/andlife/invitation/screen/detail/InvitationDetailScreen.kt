package com.andlife.invitation.screen.detail

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.andlife.designsystem.component.InvitationButton
import com.andlife.invitation.screen.guestbook.InvitationCollectionScreen
import com.andlife.ui.component.GenericTabRow
import com.andlife.ui.R

private const val TAG = "InvitationDetailScreen"

@Composable
fun InvitationDetailRoute(
    id: Long,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Log.d(TAG, "전달 받은 ID: $id")
    InvitationDetailScreen(
        modifier = modifier,
        onNavigateBack = onNavigateBack
    )
}

@Composable
private fun InvitationDetailScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
) {
    val tabTitles = listOf(
        stringResource(R.string.txt_invitation),
        stringResource(R.string.txt_guestbook),
        stringResource(R.string.txt_collection),
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
        },
        bottomBar = {
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(innerPadding)
        ) {
            InvitationButton(
                onClick = onNavigateBack,
            ) {
                Text("뒤로가기")
            }

            GenericTabRow(
                tabs = tabTitles,
                content = { index ->
                    when (index) {
                        0 -> Text("초대장 콘텐츠")
                        1 -> Text("방명록 화면")
                        2 -> InvitationCollectionScreen()
                    }
                }
            )
        }
    }
}
