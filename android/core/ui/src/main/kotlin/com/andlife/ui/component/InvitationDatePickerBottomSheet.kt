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
import com.andlife.designsystem.component.InvitationButton
import com.andlife.designsystem.component.datepicker.state.rememberInvitationDatePickerState
import com.andlife.designsystem.component.datepicker.ui.InvitationDatePicker
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.InvitationElevation
import com.andlife.designsystem.theme.InvitationSpacing
import com.andlife.designsystem.theme.InvitationTheme
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
        containerColor = InvitationTheme.colorScheme.backgroundPrimary,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = InvitationTheme.colorScheme.textSecondary,
            )
        },
        modifier = modifier,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth(),
        ) {
            InvitationDatePicker(
                state = datePickerState,
                modifier = Modifier.fillMaxWidth(),
            )

            InvitationButton(
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
                        .padding(all = InvitationSpacing.large),
                enabled = selectedDate != null,
                contentPadding = PaddingValues(vertical = InvitationSpacing.medium),
                elevation =
                    ButtonDefaults.buttonElevation(
                        defaultElevation = InvitationElevation.none,
                        pressedElevation = InvitationElevation.none,
                        disabledElevation = InvitationElevation.none,
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

@OptIn(ExperimentalMaterial3Api::class)
@PreviewTheme
@Composable
private fun InvitationDatePickerBottomSheetPreview() {
    InvitationTheme {
        InvitationDatePickerBottomSheet(
            onConfirm = {},
            onDismiss = {},
            initialDate = LocalDate(2026, 1, 31),
        )
    }
}
