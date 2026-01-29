package com.andlife.nachoserver.repository.user

import com.andlife.nachoserver.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByKakaoId(kakaoId: Long): User?
}