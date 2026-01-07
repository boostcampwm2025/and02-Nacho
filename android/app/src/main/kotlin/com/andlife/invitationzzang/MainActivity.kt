package com.andlife.invitationzzang

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.andlife.designsystem.theme.InvitationTheme
import com.andlife.invitationzzang.deeplink.InstallReferrerHandler
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainActivityViewModel by viewModels()
    private var installReferrerHandler: InstallReferrerHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate called, intent: ${intent?.data}")

        viewModel.onNewIntent(intent)

        if (savedInstanceState == null && intent.data == null) {
            checkDeferredDeepLink()
        }

        enableEdgeToEdge()
        setContent {
            InvitationTheme {
                InvitationApp()
            }
        }
    }

    private fun checkDeferredDeepLink() {
        installReferrerHandler = InstallReferrerHandler(this) { invitationId ->
            Log.d("DeferredDeepLink", "Received invitation ID: $invitationId")
            viewModel.onDeferredDeepLink(invitationId)
        }
        installReferrerHandler?.startConnection()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d("MainActivity", "onNewIntent called, intent: ${intent.data}")
        setIntent(intent)

        viewModel.onNewIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        Log.d("MainActivity", "onResume")
    }

    override fun onDestroy() {
        super.onDestroy()
        installReferrerHandler = null
        Log.d("MainActivity", "onDestroy")
    }
}
