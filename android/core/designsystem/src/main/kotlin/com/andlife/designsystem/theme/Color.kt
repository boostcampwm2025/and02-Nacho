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
import com.andlife.designsystem.preview.PreviewTheme

// Background
val BackgroundPrimary = Color(0xFFFFFFFF)
val BackgroundSecondary = Color(0xFFF3F4F6)
val BackgroundSurface = Color(0xFFFFF8F8)
val BackgroundBorder = Color(0xFFD1D5DB)
val BackgroundOverlay = Color(0x80000000)
val BackgroundInverse = Color(0xFF111111)
val BackgroundTertiary = Color(0xFFF9FAFB)

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
val IconOnSecondary = Color(0xFF374151)
val IconTertiary = Color(0xFFFFFFFF)
val IconDisabled = Color(0xFFE5E7EB)

@Immutable
data class InvitationColorScheme(
    val backgroundPrimary: Color,
    val backgroundSecondary: Color,
    val backgroundSurface: Color,
    val backgroundBorder: Color,
    val backgroundOverlay: Color,
    val backgroundTertiary: Color,
    val backgroundInverse: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val textDisabled: Color,
    val textOnPrimary: Color,
    val brandPrimary: Color,
    val brandSecondary: Color,
    val brandLight: Color,
    val brandDark: Color,
    val brandOnPrimary: Color,
    val iconPrimary: Color,
    val iconSecondary: Color,
    val iconOnSecondary: Color,
    val iconTertiary: Color,
    val iconDisabled: Color,
)

val LightInvitationColorScheme =
    InvitationColorScheme(
        backgroundPrimary = BackgroundPrimary,
        backgroundSecondary = BackgroundSecondary,
        backgroundSurface = BackgroundSurface,
        backgroundBorder = BackgroundBorder,
        backgroundOverlay = BackgroundOverlay,
        backgroundInverse = BackgroundInverse,
        backgroundTertiary = BackgroundTertiary,
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
        iconOnSecondary = IconOnSecondary,
        iconTertiary = IconTertiary,
        iconDisabled = IconDisabled,
    )

// TODO: 나중에 다크모드 색상 정의 필요. 지금은 라이트모드와 동일하게 설정
val DarkInvitationColorScheme =
    LightInvitationColorScheme.copy(
        // backgroundPrimary = TextPrimary,
    )

internal val LocalInvitationColorScheme = staticCompositionLocalOf { LightInvitationColorScheme }

@PreviewTheme
@Composable
private fun InvitationColorSchemePreview() {
    NachoTheme {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(NachoTheme.colorScheme.backgroundPrimary)
                    .padding(NachoSpacing.medium),
            verticalArrangement = Arrangement.spacedBy(NachoSpacing.large),
        ) {
            Text(
                text = "Hello",
                color = NachoTheme.colorScheme.textTertiary,
                style = NachoTheme.typography.headingLarge,
            )

            Box(
                modifier =
                    Modifier
                        .size(50.dp)
                        .background(NachoTheme.colorScheme.brandPrimary),
            )
        }
    }
}
