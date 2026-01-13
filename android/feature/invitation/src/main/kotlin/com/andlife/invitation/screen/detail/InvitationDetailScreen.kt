package com.andlife.invitation.screen.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.andlife.designsystem.component.InvitationButton
import com.andlife.invitation.screen.guestbook.InvitationCollectionRoute
import com.andlife.invitation.viewmodel.InvitationDetailViewModel
import com.andlife.ui.R
import com.andlife.ui.component.GenericTabRow
import kotlinx.collections.immutable.toImmutableList

private const val TAG = "InvitationDetailScreen"

@Composable
fun InvitationDetailRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InvitationDetailViewModel = hiltViewModel(),
) {
    val invitationId = viewModel.invitationId
    InvitationDetailScreen(
        invitationId = invitationId,
        onNavigateBack = onNavigateBack,
        modifier = modifier,
    )
}

@Composable
private fun InvitationDetailScreen(
    invitationId: Long,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tabTitles = stringArrayResource(R.array.tab_titles).toImmutableList()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
        },
        bottomBar = {
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
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
                        2 -> InvitationCollectionRoute(
                            invitationId = invitationId,
                        )
                    }
                },
            )
        }
    }
}
