package com.andlife.designsystem.component.datepicker.ui

import androidx.compose.runtime.Immutable
import com.andlife.designsystem.component.datepicker.model.InvitationDatePickerDate

@Immutable
data class InvitationDatePickerCellUiModel(
    val date: InvitationDatePickerDate,
    val day: Int,
    val isSelected: Boolean,
    val isToday: Boolean,
    val isDisabled: Boolean,
)
