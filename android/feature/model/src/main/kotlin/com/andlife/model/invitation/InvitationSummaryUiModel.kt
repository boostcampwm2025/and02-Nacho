package com.andlife.model.invitation

data class InvitationSummaryUiModel(
    val id: Long,
    val title: String,
    val displayHostName: String,
    val thumbnailUrls: List<String>,
    val address: String,
    val invitationDateTime: String,
    val dDay: String
)
