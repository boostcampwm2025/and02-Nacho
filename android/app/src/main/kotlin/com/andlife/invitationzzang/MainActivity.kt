package com.andlife.invitationzzang

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.andlife.deeplink.InstallReferrerHandler
import com.andlife.designsystem.theme.InvitationTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var installReferrerHandler: InstallReferrerHandler
    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate called, intent: ${intent?.data}")
        viewModel.onNewIntent(intent)
        if (savedInstanceState == null && intent.data == null) {
            installReferrerHandler.startConnection()
        }

        enableEdgeToEdge()
        setContent {
            InvitationTheme {
                InvitationApp()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d("MainActivity", "onNewIntent called, intent: ${intent.data}")
        setIntent(intent)
        viewModel.onNewIntent(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        installReferrerHandler.endConnection()
    }
}
