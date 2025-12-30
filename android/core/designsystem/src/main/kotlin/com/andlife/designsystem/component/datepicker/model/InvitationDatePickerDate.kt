package com.andlife.designsystem.component.datepicker.model

import androidx.compose.runtime.Immutable
import kotlinx.datetime.LocalDate

@Immutable
data class InvitationDatePickerDate(
    val date: LocalDate,
)
