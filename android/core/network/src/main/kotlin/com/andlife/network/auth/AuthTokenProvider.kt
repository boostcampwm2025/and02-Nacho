package com.andlife.network.auth

interface AuthTokenProvider {
    fun getUserId(): Long?
    fun getInvitationIds(): List<Long>
}
