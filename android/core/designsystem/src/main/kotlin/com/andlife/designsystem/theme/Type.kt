package com.andlife.designsystem.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andlife.designsystem.R
import com.andlife.designsystem.util.DevicePreview
import com.andlife.designsystem.util.ThemePreview

val pretendardFamily = FontFamily(
    Font(R.font.pretendard_bold, FontWeight.Bold),
    Font(R.font.pretendard_semi_bold, FontWeight.SemiBold),
    Font(R.font.pretendard_medium, FontWeight.Medium),
    Font(R.font.pretendard_regular, FontWeight.Normal),
)

object InvitationLineHeightRatio {
    const val Heading = 1.5f
    const val Body = 1.3f
}

fun TextStyle.withLineHeightRatio(ratio: Float): TextStyle =
    copy(lineHeight = fontSize * ratio)

@Immutable
data class InvitationTypography(
    val heading1: TextStyle,
    val heading2: TextStyle,
    val heading3: TextStyle,
    val heading4: TextStyle,
    val bodyLarge1: TextStyle,
    val bodyLarge2: TextStyle,
    val bodyLarge3: TextStyle,
    val bodyMedium1: TextStyle,
    val bodyMedium2: TextStyle,
    val bodyMedium3: TextStyle,
    val bodySmall1: TextStyle,
    val bodySmall2: TextStyle,
    val bodySmall3: TextStyle,
    val bodyExtraSmall1: TextStyle,
    val bodyExtraSmall2: TextStyle,
    val bodyExtraSmall3: TextStyle,
)

internal val invitationTypography = InvitationTypography(

    heading1 = TextStyle(
        fontFamily = pretendardFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ).withLineHeightRatio(InvitationLineHeightRatio.Heading),

    heading2 = TextStyle(
        fontFamily = pretendardFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    ).withLineHeightRatio(InvitationLineHeightRatio.Heading),

    heading3 = TextStyle(
        fontFamily = pretendardFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
    ).withLineHeightRatio(InvitationLineHeightRatio.Heading),

    heading4 = TextStyle(
        fontFamily = pretendardFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp
    ).withLineHeightRatio(InvitationLineHeightRatio.Heading),

    bodyLarge1 = TextStyle(
        fontFamily = pretendardFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp
    ).withLineHeightRatio(InvitationLineHeightRatio.Body),

    bodyLarge2 = TextStyle(
        fontFamily = pretendardFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    ).withLineHeightRatio(InvitationLineHeightRatio.Body),

    bodyLarge3 = TextStyle(
        fontFamily = pretendardFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ).withLineHeightRatio(InvitationLineHeightRatio.Body),

    bodyMedium1 = TextStyle(
        fontFamily = pretendardFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
    ).withLineHeightRatio(InvitationLineHeightRatio.Body),

    bodyMedium2 = TextStyle(
        fontFamily = pretendardFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ).withLineHeightRatio(InvitationLineHeightRatio.Body),

    bodyMedium3 = TextStyle(
        fontFamily = pretendardFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ).withLineHeightRatio(InvitationLineHeightRatio.Body),

    bodySmall1 = TextStyle(
        fontFamily = pretendardFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp
    ).withLineHeightRatio(InvitationLineHeightRatio.Body),

    bodySmall2 = TextStyle(
        fontFamily = pretendardFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
    ).withLineHeightRatio(InvitationLineHeightRatio.Body),

    bodySmall3 = TextStyle(
        fontFamily = pretendardFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ).withLineHeightRatio(InvitationLineHeightRatio.Body),

    bodyExtraSmall1 = TextStyle(
        fontFamily = pretendardFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp
    ).withLineHeightRatio(InvitationLineHeightRatio.Body),

    bodyExtraSmall2 = TextStyle(
        fontFamily = pretendardFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp
    ).withLineHeightRatio(InvitationLineHeightRatio.Body),

    bodyExtraSmall3 = TextStyle(
        fontFamily = pretendardFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp
    ).withLineHeightRatio(InvitationLineHeightRatio.Body)
)

internal val LocalInvitationTypography = staticCompositionLocalOf { invitationTypography }

@ThemePreview
@DevicePreview
@Composable
private fun InvitationTypographyPreview() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
    ) {
        Text("Heading 1", style = InvitationTheme.typography.heading1)
        Text("Heading 2", style = InvitationTheme.typography.heading2)
        Text("Heading 3", style = InvitationTheme.typography.heading3)
        Text("Heading 4", style = InvitationTheme.typography.heading4)
        Text("Body Large 1", style = InvitationTheme.typography.bodyLarge1)
        Text("Body Large 2", style = InvitationTheme.typography.bodyLarge2)
        Text("Body Large 3", style = InvitationTheme.typography.bodyLarge3)
        Text("Body Medium 1", style = InvitationTheme.typography.bodyMedium1)
        Text("Body Medium 2", style = InvitationTheme.typography.bodyMedium2)
        Text("Body Medium 3", style = InvitationTheme.typography.bodyMedium3)
        Text("Body Small 1", style = InvitationTheme.typography.bodySmall1)
        Text("Body Small 2", style = InvitationTheme.typography.bodySmall2)
        Text("Body Small 3", style = InvitationTheme.typography.bodySmall3)
        Text("Body Extra Small 1", style = InvitationTheme.typography.bodyExtraSmall1)
        Text("Body Extra Small 2", style = InvitationTheme.typography.bodyExtraSmall2)
        Text("Body Extra Small 3", style = InvitationTheme.typography.bodyExtraSmall3)
    }
}