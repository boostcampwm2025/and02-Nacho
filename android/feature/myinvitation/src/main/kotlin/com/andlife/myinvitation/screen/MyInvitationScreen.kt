package com.andlife.myinvitation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.andlife.designsystem.theme.InvitationTheme
import com.andlife.ui.component.InvitationTimePickerBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyInvitationScreen(modifier: Modifier = Modifier) {
    var showSheet by remember { mutableStateOf(false) }
    var selectedTimeText by remember { mutableStateOf("시간을 선택해주세요") }
    var initialHour by remember { mutableIntStateOf(9) }
    var initialMinute by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = selectedTimeText, style = InvitationTheme.typography.headingLarge)

        Button(onClick = { showSheet = true }) {
            Text("시간 변경하기")
        }
    }

    // 바텀시트 호출
    if (showSheet) {
        InvitationTimePickerBottomSheet(
            initialHour = initialHour,
            initialMinute = initialMinute,
            onConfirm = { hour, minute ->
                initialHour = hour
                initialMinute = minute
                val period = if (hour >= 12) "PM" else "AM"
                val hour12 = if (hour % 12 == 0) 12 else hour % 12
                selectedTimeText = "선택된 시간: $period $hour12:${minute.toString().padStart(2, '0')}"
                showSheet = false
            },
            onDismissRequest = {
                showSheet = false
            },
        )
    }
}
