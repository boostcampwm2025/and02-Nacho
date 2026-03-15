package com.andlife.nachoserver.repository.user

import com.andlife.nachoserver.entity.FcmToken
import com.andlife.nachoserver.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface FcmTokenRepository : JpaRepository<FcmToken, String> {
    fun findByUser(user: User): FcmToken?
    fun deleteByUser(user: User)
}