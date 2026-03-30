package com.andlife.ui.util

import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldState
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.staticCompositionLocalOf

val LocalNavigationSuiteState = staticCompositionLocalOf<NavigationSuiteScaffoldState> {
    error("Navigation Suite State 설정되지 않음")
}

val NavigationSuiteType.isNavigationBar
    get() =
        this == NavigationSuiteType.ShortNavigationBarCompact ||
            this == NavigationSuiteType.ShortNavigationBarMedium ||
            this == NavigationSuiteType.NavigationBar
