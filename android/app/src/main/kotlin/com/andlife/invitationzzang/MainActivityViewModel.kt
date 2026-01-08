package com.andlife.invitationzzang

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andlife.deeplink.DeepLinkConfig
import com.andlife.deeplink.DeepLinkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    val deepLinkManager: DeepLinkManager
) : ViewModel() {
    private val _deepLinkEvent = Channel<Intent>(capacity = Channel.BUFFERED)
    val deepLinkEvent = _deepLinkEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            deepLinkManager.deferredDeepLinkId.collect { id ->
                val intent = createDeepLinkIntent(id)
                _deepLinkEvent.send(intent)
            }
        }
    }

    fun onNewIntent(intent: Intent?) {
        intent?.data ?: return
        viewModelScope.launch {
            _deepLinkEvent.send(intent)
        }
    }

    private fun createDeepLinkIntent(invitationId: String): Intent {
        val uri = DeepLinkConfig.buildInvitationDeepLinkUri(invitationId.toLong())
        return Intent(Intent.ACTION_VIEW, uri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
    }
}
