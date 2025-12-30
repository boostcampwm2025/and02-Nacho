package com.andlife.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.andlife.designsystem.component.InvitationButton
import com.andlife.designsystem.component.timepicker.InvitationTimePicker
import com.andlife.designsystem.component.timepicker.rememberInvitationTimePickerState
import com.andlife.designsystem.theme.InvitationElevation
import com.andlife.designsystem.theme.InvitationSpacing
import com.andlife.designsystem.theme.InvitationTheme
import com.andlife.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvitationTimePickerBottomSheet(
    onConfirm: (hour: Int, minute: Int) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    initialHour: Int = 9,
    initialMinute: Int = 0,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
) {
    val timePickerState =
        rememberInvitationTimePickerState(
            initialHour = initialHour,
            initialMinute = initialMinute,
        )

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = InvitationTheme.colorScheme.backgroundPrimary,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = InvitationTheme.colorScheme.backgroundBorder,
            )
        },
    ) {
        Column(
            modifier =
                modifier
                    .fillMaxWidth()
                    .padding(horizontal = InvitationSpacing.large)
                    .padding(bottom = InvitationSpacing.large),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(InvitationSpacing.xLarge),
        ) {
            InvitationTimePicker(
                state = timePickerState,
            )
            InvitationButton(
                onClick = {
                    onConfirm(timePickerState.hour, timePickerState.minute)
                },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = InvitationSpacing.large),
                elevation =
                    ButtonDefaults.buttonElevation(
                        defaultElevation = InvitationElevation.none,
                        pressedElevation = InvitationElevation.none,
                    ),
            ) {
                Text(
                    text = stringResource(id = R.string.btn_label_confirm),
                    style = InvitationTheme.typography.bodyLargeSemiBold,
                )
            }
        }
    }
}
