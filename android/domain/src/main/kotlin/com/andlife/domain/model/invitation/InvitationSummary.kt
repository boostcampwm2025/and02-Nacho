package com.andlife.domain.model.invitation

data class InvitationSummary(
    val id: Long,
    val title: String,
    val displayHostName: String,
    val thumbnailUrls: List<String>,
    val invitationDate: String,
    val startTime: String,
    val address: String,
    val isOwner: Boolean,
)
