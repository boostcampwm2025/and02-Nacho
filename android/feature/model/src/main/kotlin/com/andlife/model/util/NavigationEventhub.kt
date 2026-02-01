package com.andlife.model.util

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

object NavigationEventhub {

    private val _refreshEvent = Channel<RefreshTarget>(Channel.CONFLATED)
    val refreshEvent = _refreshEvent.receiveAsFlow()

    fun emit(target: RefreshTarget) {
        _refreshEvent.trySend(target)
    }

    enum class RefreshTarget {
        HOME, INVITATION, MY_INVITATION, ALL
    }

}
