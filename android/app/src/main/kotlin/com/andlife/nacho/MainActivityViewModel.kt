package com.andlife.nacho

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andlife.deeplink.DeepLinkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    val deepLinkManager: DeepLinkManager,
) : ViewModel() {
    private var lastProcessedId: String? = null

    private val _deepLinkEvent = Channel<Intent>(capacity = Channel.BUFFERED)
    val deepLinkEvent = _deepLinkEvent.receiveAsFlow()

    private val _navigateToDetail = Channel<Long>(capacity = Channel.BUFFERED)
    val navigateToDetail = _navigateToDetail.receiveAsFlow()

    init {
        handleDeferredDeepLink()
    }

    fun handleDeepLink(intent: Intent?) {
        val data = intent?.data ?: return
        viewModelScope.launch {
            _deepLinkEvent.send(intent)
        }
    }

    private fun handleDeferredDeepLink() {
        viewModelScope.launch {
            deepLinkManager.deferredDeepLinkId.collect { invitationId ->
                if (invitationId != null && invitationId != lastProcessedId) {
                    lastProcessedId = invitationId

                    invitationId.toLongOrNull()?.let { id ->
                        _navigateToDetail.send(id)
                        deepLinkManager.clearInvitationId()
                    }
                }
            }
        }
    }
}
