package com.andlife.data.repository.user.mapper

import com.andlife.domain.model.auth.User
import com.andlife.network.model.auth.UserResponse

fun UserResponse.toDomain(): User {
    return User(
        id = id,
        name = name,
        email = email,
        profileImageUrl = profileImageUrl
    )
}
