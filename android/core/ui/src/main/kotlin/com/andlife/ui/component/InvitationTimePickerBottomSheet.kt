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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.andlife.designsystem.component.NachoButton
import com.andlife.designsystem.component.timepicker.NachoTimePicker
import com.andlife.designsystem.component.timepicker.rememberInvitationTimePickerState
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoElevation
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.ui.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvitationTimePickerBottomSheet(
    onConfirm: (hour: Int, minute: Int) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    initialHour: Int = 9,
    initialMinute: Int = 0,
) {
    val sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    val timePickerState =
        rememberInvitationTimePickerState(
            initialHour = initialHour,
            initialMinute = initialMinute,
        )

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = NachoTheme.colorScheme.backgroundPrimary,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = NachoTheme.colorScheme.backgroundBorder,
            )
        },
        modifier = modifier,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = NachoSpacing.large)
                    .padding(bottom = NachoSpacing.large),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(NachoSpacing.xLarge),
        ) {
            NachoTimePicker(
                state = timePickerState,
            )
            NachoButton(
                onClick = {
                    onConfirm(timePickerState.hour, timePickerState.minute)
                    scope
                        .launch {
                            sheetState.hide()
                        }.invokeOnCompletion { onDismissRequest() }
                },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = NachoSpacing.large),
                elevation =
                    ButtonDefaults.buttonElevation(
                        defaultElevation = NachoElevation.none,
                        pressedElevation = NachoElevation.none,
                    ),
            ) {
                Text(
                    text = stringResource(id = R.string.btn_label_confirm),
                    style = NachoTheme.typography.bodyLargeSemiBold,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewTheme
@Composable
private fun NachoTimePickerBottomSheetPreview() {
    NachoTheme {
        InvitationTimePickerBottomSheet(
            onConfirm = { _, _ -> },
            onDismissRequest = {},
        )
    }
}
