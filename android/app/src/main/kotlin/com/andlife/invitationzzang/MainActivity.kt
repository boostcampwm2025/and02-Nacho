package com.andlife.invitationzzang

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.andlife.designsystem.theme.InvitationTheme
import com.andlife.invitationzzang.deeplink.InstallReferrerHandler
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var currentIntent by mutableStateOf<Intent?>(null)
    private var installReferrerHandler: InstallReferrerHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate called, intent: ${intent?.data}")

        if (savedInstanceState == null && intent.data == null) {
            checkDeferredDeepLink()
        } else {
            currentIntent = intent
        }

        enableEdgeToEdge()
        setContent {
            InvitationTheme {
                InvitationApp(
                    deepLinkIntent = currentIntent
                )
            }
        }
    }

    private fun checkDeferredDeepLink() {
        installReferrerHandler = InstallReferrerHandler(this) { invitationId ->
            Log.d("DeferredDeepLink", "Received invitation ID from Install Referrer: $invitationId")

            val deepLinkUri = Uri.parse("https://invitationzzang.com/invite/$invitationId")
            val deepLinkIntent = Intent(Intent.ACTION_VIEW, deepLinkUri)

            currentIntent = deepLinkIntent
        }
        installReferrerHandler?.startConnection()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d("MainActivity", "onNewIntent called, intent: ${intent.data}")
        setIntent(intent)

        currentIntent = intent
    }

    override fun onStart() {
        super.onStart()
        Log.d("MainActivity", "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("MainActivity", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("MainActivity", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("MainActivity", "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        installReferrerHandler = null
        Log.d("MainActivity", "onDestroy")
    }
}
