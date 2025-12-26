package com.andlife.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

// Theme.kt
@Composable
fun InvitationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkInvitationColorScheme else LightInvitationColorScheme

    CompositionLocalProvider(
        LocalInvitationColorScheme provides colorScheme,
        LocalInvitationTypography provides invitationTypography,
        LocalInvitationShapes provides invitationShapes,
    ) {
        MaterialTheme(
            content = content,
        )
    }
}

object InvitationTheme {

    val colorScheme: InvitationColorScheme
        @Composable
        get() = LocalInvitationColorScheme.current

    val typography: InvitationTypography
        @Composable
        get() = LocalInvitationTypography.current

    val shapes: InvitationShapes
        @Composable
        get() = LocalInvitationShapes.current
}