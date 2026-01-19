package com.andlife.model.common

import com.andlife.domain.model.guestbook.Author

data class AuthorUiModel(
    val id: Long,
    val name: String,
    val profileImageUrl: String? = null,
)

fun Author.toUiModel(): AuthorUiModel = AuthorUiModel(
    id = id,
    name = name,
    profileImageUrl = profileImageUrl,
)
