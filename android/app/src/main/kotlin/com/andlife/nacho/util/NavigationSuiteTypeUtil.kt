package com.andlife.nacho.util

import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType

internal val NavigationSuiteType.isNavigationBar
    get() =
        this == NavigationSuiteType.ShortNavigationBarCompact ||
            this == NavigationSuiteType.ShortNavigationBarMedium ||
            this == NavigationSuiteType.NavigationBar
