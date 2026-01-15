package com.andlife.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.andlife.designsystem.component.NachoButton
import com.andlife.designsystem.component.datepicker.state.rememberInvitationDatePickerState
import com.andlife.designsystem.component.datepicker.ui.NachoDatePicker
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoElevation
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.ui.R
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvitationDatePickerBottomSheet(
    onConfirm: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    initialDate: LocalDate? = null,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    val datePickerState =
        rememberInvitationDatePickerState(
            initialSelectedDate = initialDate,
        )

    val selectedDate = datePickerState.selectedDate

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = NachoTheme.colorScheme.backgroundPrimary,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = NachoTheme.colorScheme.textSecondary,
            )
        },
        modifier = modifier,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth(),
        ) {
            NachoDatePicker(
                state = datePickerState,
                modifier = Modifier.fillMaxWidth(),
            )

            NachoButton(
                onClick = {
                    selectedDate?.let {
                        onConfirm(it)
                        scope
                            .launch {
                                sheetState.hide()
                            }.invokeOnCompletion { onDismiss() }
                    }
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(all = NachoSpacing.large),
                enabled = selectedDate != null,
                contentPadding = PaddingValues(vertical = NachoSpacing.medium),
                elevation =
                    ButtonDefaults.buttonElevation(
                        defaultElevation = NachoElevation.none,
                        pressedElevation = NachoElevation.none,
                        disabledElevation = NachoElevation.none,
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
private fun InvitationDatePickerBottomSheetPreview() {
    NachoTheme {
        InvitationDatePickerBottomSheet(
            onConfirm = {},
            onDismiss = {},
            initialDate = LocalDate(2026, 1, 31),
        )
    }
}
