package com.andlife.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun InvitationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val backgroundColorScheme = if (darkTheme) DarkBackgroundColorScheme else LightBackgroundColorScheme
    val textColorScheme = if (darkTheme) DarkTextColorScheme else LightTextColorScheme
    val iconColorScheme = if (darkTheme) DarkIconColorScheme else LightIconColorScheme
    val brandColorScheme = if (darkTheme) DarkBrandColorScheme else LightBrandColorScheme

    CompositionLocalProvider(
        LocalBackgroundColorScheme provides backgroundColorScheme,
        LocalTextColorScheme provides textColorScheme,
        LocalIconColorScheme provides iconColorScheme,
        LocalBrandColorScheme provides brandColorScheme,
        LocalInvitationTypography provides invitationTypography,
    ) {
        MaterialTheme(
            content = content,
        )
    }
}

object InvitationTheme {

    val backgroundColorScheme: InvitationColorScheme
        @Composable
        get() = LocalBackgroundColorScheme.current

    val textColorScheme: InvitationColorScheme
        @Composable
        get() = LocalTextColorScheme.current

    val iconColorScheme: InvitationColorScheme
        @Composable
        get() = LocalIconColorScheme.current

    val brandColorScheme: InvitationBrandColorScheme
        @Composable
        get() = LocalBrandColorScheme.current

    val typography: InvitationTypography
        @Composable
        get() = LocalInvitationTypography.current
}