package com.andlife.designsystem.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.andlife.designsystem.preview.ThemePreview

// Background
val BackgroundPrimary = Color(0xFFFFFFFF)
val BackgroundSecondary = Color(0xFFF3F4F6)
val BackgroundSurface = Color(0xFFFFF8F8)
val BackgroundBorder = Color(0xFFD1D5DB)
val BackgroundOverlay = Color(0x80000000)

// Text
val TextPrimary = Color(0xFF111827)
val TextSecondary = Color(0xFF4B5563)
val TextTertiary = Color(0xFF9CA3AF)
val TextDisabled = Color(0xFFD1D5DB)
val TextOnPrimary = Color(0xFFFFFFFF)

// Brand
val BrandPrimary = Color(0xFFF43F5E)
val BrandOnPrimary = Color(0xFFFFFFFF)
val BrandSecondary = Color(0xFFFB7185)
val BrandLight = Color(0xFFFFF1F2)
val BrandDark = Color(0xFFBE123C)

// Icon
val IconPrimary = Color(0xFFF43F5E)
val IconSecondary = Color(0xFF111827)
val IconTertiary = Color(0xFFFFFFFF)

// ColorScheme.kt
@Immutable
data class InvitationColorScheme(
    // Background
    val backgroundPrimary: Color,
    val backgroundSecondary: Color,
    val backgroundSurface: Color,
    val backgroundBorder: Color,
    val backgroundOverlay: Color,

    // Text
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val textDisabled: Color,
    val textOnPrimary: Color,

    // Brand
    val brandPrimary: Color,
    val brandSecondary: Color,
    val brandLight: Color,
    val brandDark: Color,
    val brandOnPrimary: Color,

    // Icon
    val iconPrimary: Color,
    val iconSecondary: Color,
    val iconTertiary: Color,
)

val LightInvitationColorScheme = InvitationColorScheme(
    backgroundPrimary = BackgroundPrimary,
    backgroundSecondary = BackgroundSecondary,
    backgroundSurface = BackgroundSurface,
    backgroundBorder = BackgroundBorder,
    backgroundOverlay = BackgroundOverlay,

    textPrimary = TextPrimary,
    textSecondary = TextSecondary,
    textTertiary = TextTertiary,
    textDisabled = TextDisabled,
    textOnPrimary = TextOnPrimary,

    brandPrimary = BrandPrimary,
    brandSecondary = BrandSecondary,
    brandLight = BrandLight,
    brandDark = BrandDark,
    brandOnPrimary = BrandOnPrimary,

    iconPrimary = IconPrimary,
    iconSecondary = IconSecondary,
    iconTertiary = IconTertiary,
)

// TODO: 나중에 다크모드 색상 정의 필요. 지금은 라이트모드와 동일하게 설정
val DarkInvitationColorScheme = LightInvitationColorScheme.copy(
    //backgroundPrimary = TextPrimary,
)

internal val LocalInvitationColorScheme = staticCompositionLocalOf { LightInvitationColorScheme }

@ThemePreview
@Composable
private fun InvitationColorSchemePreview() {
    InvitationTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(InvitationTheme.colorScheme.backgroundPrimary)
                .padding(InvitationSpacing.medium),
            verticalArrangement = Arrangement.spacedBy(InvitationSpacing.large)
        ) {
            Text(
                text = "Hello",
                color = InvitationTheme.colorScheme.textTertiary,
                style = InvitationTheme.typography.heading1
            )

            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(InvitationTheme.colorScheme.brandPrimary)
            )
        }
    }
}