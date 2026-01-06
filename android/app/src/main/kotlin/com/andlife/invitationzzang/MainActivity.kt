package com.andlife.invitationzzang

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavHostController
import com.andlife.designsystem.theme.InvitationTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var navController: NavHostController? = null
    private var lastHandledIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate called, intent: ${intent?.data}")
        enableEdgeToEdge()
        setContent {
            InvitationTheme {
                InvitationApp(
                    onNavControllerCreated = { controller ->
                        navController = controller
                        Log.d("MainActivity", "NavController 저장됨")
                    }
                )
            }
        }
        lastHandledIntent = intent
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d("MainActivity", "onNewIntent called, intent: ${intent.data}")
        setIntent(intent)

        if (intent.dataString == lastHandledIntent?.dataString) {
            return
        }

        lastHandledIntent = intent
        navController?.handleDeepLink(intent)

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
        Log.d("MainActivity", "onDestroy")
    }
}
