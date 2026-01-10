package com.andlife.invitationzzang

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
    private val _deepLinkEvent = Channel<Intent>(capacity = Channel.BUFFERED)
    val deepLinkEvent = _deepLinkEvent.receiveAsFlow()

    fun onNewIntent(intent: Intent?) {
        intent?.data ?: return
        viewModelScope.launch {
            _deepLinkEvent.send(intent)
        }
    }
}
