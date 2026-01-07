package com.andlife.invitationzzang

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor() : ViewModel() {
    private val _deepLinkIntent = MutableSharedFlow<Intent>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val deepLinkIntent = _deepLinkIntent.asSharedFlow()

    fun onNewIntent(intent: Intent?) {
        intent?.data ?: return
        _deepLinkIntent.tryEmit(intent)
    }

    fun onDeferredDeepLink(invitationId: String) {
        val uri = Uri.parse("https://invitationzzang.com/invite/$invitationId")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        _deepLinkIntent.tryEmit(intent)
    }
}
