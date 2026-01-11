package com.andlife.invitationzzang

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.andlife.designsystem.theme.InvitationTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate called, intent: ${intent?.data}")
        viewModel.handleDeepLink(intent)

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
        viewModel.handleDeepLink(intent)
    }
}
