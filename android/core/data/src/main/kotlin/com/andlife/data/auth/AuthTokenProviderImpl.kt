package com.andlife.data.auth

import com.andlife.datastore.UserStorage
import com.andlife.network.auth.AuthTokenProvider
import javax.inject.Inject

class AuthTokenProviderImpl @Inject constructor(
    private val userStorage: UserStorage
) : AuthTokenProvider {
    override fun getUserId(): Long? = userStorage.getUserId()
    override fun getInvitationIds(): List<Long> = userStorage.getInvitationIds()
}
