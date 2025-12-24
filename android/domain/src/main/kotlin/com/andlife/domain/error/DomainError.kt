package com.andlife.domain.error

sealed interface DomainError: InvitationError {
    enum class Validation: DomainError {
        INVALID_TITLE,
        INVALID_NAME,
        INVALID_DATE,
        INVALID_START_TIME,
        INVALID_PLACE,
        INVALID_INVITATION_CARD
    }
}