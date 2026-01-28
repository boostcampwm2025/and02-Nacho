package com.andlife.nacho

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.login.LocalLoginManager
import com.andlife.login.social.LoginManager
import com.andlife.nacho.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var loginManager: LoginManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.handleDeepLink(intent)

        enableEdgeToEdge()
        setContent {
            NachoTheme {
                CompositionLocalProvider(LocalLoginManager provides loginManager) {
                    NachoApp()
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        viewModel.handleDeepLink(intent)
    }
}
