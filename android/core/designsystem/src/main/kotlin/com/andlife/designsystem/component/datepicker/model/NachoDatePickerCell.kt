package com.andlife.designsystem.component.datepicker.model

import androidx.compose.runtime.Immutable

@Immutable
data class NachoDatePickerCell(
    val date: NachoDatePickerDate,
    val day: Int,
    val isSelected: Boolean,
    val isToday: Boolean,
    val isDisabled: Boolean,
)
