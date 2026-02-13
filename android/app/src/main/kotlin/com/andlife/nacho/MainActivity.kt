package com.andlife.nacho

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
        val splashScreen = installSplashScreen()
        viewModel.handleDeepLink(intent)
        splashScreen.setKeepOnScreenCondition {
            viewModel.uiState.value.isSplash
        }
        enableEdgeToEdge()
        setContent {
            NachoTheme {
                CompositionLocalProvider(LocalLoginManager provides loginManager) {
                    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                    if (!uiState.isSplash) {
                        NachoApp(
                            startDestination = uiState.startDestination
                        )
                    }
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

val LocalLoginManager = staticCompositionLocalOf<LoginManager> {
    error("LoginManager가 설정되지 않았습니다.")
}
