package com.andlife.domain.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object RefreshEventHub {
    private val _homeRefresh = MutableStateFlow(false)
    private val _invitationRefresh = MutableStateFlow(false)
    private val _myInvitationRefresh = MutableStateFlow(false)

    val homeRefresh = _homeRefresh.asStateFlow()
    val invitationRefresh = _invitationRefresh.asStateFlow()
    val myInvitationRefresh = _myInvitationRefresh.asStateFlow()

    fun emit(target: RefreshTarget) {
        when (target) {
            RefreshTarget.HOME -> _homeRefresh.value = true
            RefreshTarget.INVITATION -> _invitationRefresh.value = true
            RefreshTarget.MY_INVITATION -> _myInvitationRefresh.value = true
            RefreshTarget.ALL -> {
                _homeRefresh.value = true
                _invitationRefresh.value = true
                _myInvitationRefresh.value = true
            }
        }
    }

    fun consumeHome() { _homeRefresh.value = false }
    fun consumeInvitation() { _invitationRefresh.value = false }
    fun consumeMyInvitation() { _myInvitationRefresh.value = false }

    enum class RefreshTarget { HOME, INVITATION, MY_INVITATION, ALL }
}
