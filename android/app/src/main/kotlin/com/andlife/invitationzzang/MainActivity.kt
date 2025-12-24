package com.andlife.invitationzzang

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.andlife.home.homeNavGraph
import com.andlife.invitation.invitationNavGraph
import com.andlife.invitationzzang.navigation.MainBottomTab
import com.andlife.invitationzzang.navigation.rememberInvitationNavigator
import com.andlife.invitationzzang.ui.theme.InvitationTheme
import com.andlife.myinvitation.myInvitationNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val navigator = rememberInvitationNavigator(navController)
            InvitationTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        TestBottomBar(
                            visible = navigator.shouldShowBottomBar(),
                            tabs = navigator.mainBottomTabs.toList(),
                            currentTab = navigator.currentTab,
                            onTabSelected = { navigator.navigate(it) }
                        )
                    }) { innerPadding ->
                    NavHost(
                        modifier = Modifier.padding(innerPadding),
                        navController = navigator.navController,
                        startDestination = navigator.startDestination
                    ) {
                        homeNavGraph()
                        invitationNavGraph()
                        myInvitationNavGraph()
                    }
                }
            }
        }
    }
}

@Composable
private fun TestBottomBar(
    visible: Boolean,
    tabs: List<MainBottomTab>,
    currentTab: MainBottomTab?,
    onTabSelected: (MainBottomTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar() {
        tabs.forEach { tab ->
            val selected = tab == currentTab
            NavigationBarItem(
                selected = selected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(painterResource(tab.iconResId), null)
                },
                label = {
                    Text(stringResource(tab.labelResId))
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedTextColor = if (selected) Color.Black else Color.Gray,
                    selectedIconColor = if (selected) Color.Red else Color.Gray
                )
            )
        }
    }
}