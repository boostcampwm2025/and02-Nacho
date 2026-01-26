package com.andlife.nachoserver.auth

sealed class AuthContext {
    data class Member(val userId: Long) : AuthContext()
    data class Guest(val invitationIds: List<Long>): AuthContext()
}