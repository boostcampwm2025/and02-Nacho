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
    modifier: Modifier = Modifier,
) {
    MyInvitationScreen(
        onNavigateToCreate = onNavigateToCreate,
        modifier = modifier,
    )
}

@Composable
private fun MyInvitationScreen(
    onNavigateToCreate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Text(text = "MyInvitationScreen")
            NachoButton(
                onClick = onNavigateToCreate,
            ) {
                Text("초대 생성")
            }
        }
    }
}
