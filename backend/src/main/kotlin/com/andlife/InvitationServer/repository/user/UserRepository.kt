package com.andlife.InvitationServer.repository.user

import com.andlife.InvitationServer.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long>