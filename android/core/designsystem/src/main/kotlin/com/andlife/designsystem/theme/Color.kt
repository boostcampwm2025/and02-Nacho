package com.andlife.designsystem.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.andlife.designsystem.util.ThemePreview

object InvitationColors {
    // BackGround
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
}

@Immutable
class InvitationColorScheme(
    val primary: Color,
    val secondary: Color,
    val tertiary: Color = Color.Unspecified,
    val disabled: Color = Color.Unspecified,
    val surface: Color = Color.Unspecified,
    val onPrimary: Color = Color.Unspecified,
    val border: Color = Color.Unspecified,
    val overlay: Color = Color.Unspecified,
)

@Immutable
class InvitationBrandColorScheme(
    val primary: Color,
    val secondary: Color,
    val light: Color,
    val dark: Color,
    val onPrimary: Color,
)

val LightBackgroundColorScheme = InvitationColorScheme(
    primary = InvitationColors.BackgroundPrimary,
    secondary = InvitationColors.BackgroundSecondary,
    surface = InvitationColors.BackgroundSurface,
    border = InvitationColors.BackgroundBorder,
    overlay = InvitationColors.BackgroundOverlay,
)

val LightTextColorScheme = InvitationColorScheme(
    primary = InvitationColors.TextPrimary,
    secondary = InvitationColors.TextSecondary,
    tertiary = InvitationColors.TextTertiary,
    disabled = InvitationColors.TextDisabled,
    onPrimary = InvitationColors.TextOnPrimary,
)

val LightIconColorScheme = InvitationColorScheme(
    primary = InvitationColors.IconPrimary,
    secondary = InvitationColors.IconSecondary,
    tertiary = InvitationColors.IconTertiary,
)

val LightBrandColorScheme = InvitationBrandColorScheme(
    primary = InvitationColors.BrandPrimary,
    secondary = InvitationColors.BrandSecondary,
    light = InvitationColors.BrandLight,
    dark = InvitationColors.BrandDark,
    onPrimary = InvitationColors.BrandOnPrimary,
)

// TODO: 나중에 다크모드 색상 정의 필요. 지금은 라이트모드와 동일하게 설정
val DarkBackgroundColorScheme = LightBackgroundColorScheme
val DarkTextColorScheme = LightTextColorScheme
val DarkIconColorScheme = LightIconColorScheme
val DarkBrandColorScheme = LightBrandColorScheme

internal val LocalBackgroundColorScheme = staticCompositionLocalOf { LightBackgroundColorScheme }
internal val LocalTextColorScheme = staticCompositionLocalOf { LightTextColorScheme }
internal val LocalIconColorScheme = staticCompositionLocalOf { LightIconColorScheme }
internal val LocalBrandColorScheme = staticCompositionLocalOf { LightBrandColorScheme }

@ThemePreview
@Composable
private fun InvitationColorSchemePreview() {
    InvitationTheme {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            Text(
                text = "Hello",
                color = InvitationTheme.textColorScheme.tertiary,
                style = InvitationTheme.typography.heading1
            )

            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(InvitationTheme.backgroundColorScheme.overlay)
            )
        }
    }
}