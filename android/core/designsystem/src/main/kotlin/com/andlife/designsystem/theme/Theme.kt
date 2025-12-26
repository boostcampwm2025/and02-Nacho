package com.andlife.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun InvitationTheme(
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalInvitationTypography provides InvitationTheme.typography,
    ) { }
}

object InvitationTheme {
    val typography: InvitationTypography
        @Composable
        get() = LocalInvitationTypography.current
}