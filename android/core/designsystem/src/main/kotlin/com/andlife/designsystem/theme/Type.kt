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
import com.andlife.designsystem.preview.PreviewTheme

val pretendardFamily =
    FontFamily(
        Font(R.font.pretendard_bold, FontWeight.Bold),
        Font(R.font.pretendard_semi_bold, FontWeight.SemiBold),
        Font(R.font.pretendard_medium, FontWeight.Medium),
        Font(R.font.pretendard_regular, FontWeight.Normal),
    )

object InvitationLineHeightRatio {
    const val HEADING = 1.5f
    const val BODY = 1.3f
}

fun TextStyle.withLineHeightRatio(ratio: Float): TextStyle = copy(lineHeight = fontSize * ratio)

@Immutable
data class InvitationTypography(
    val headingLarge: TextStyle,
    val headingMedium: TextStyle,
    val headingSmallBold: TextStyle,
    val headingSmallSemiBold: TextStyle,
    val bodyLargeSemiBold: TextStyle,
    val bodyLargeMedium: TextStyle,
    val bodyLargeRegular: TextStyle,
    val bodyMediumSemiBold: TextStyle,
    val bodyMediumMedium: TextStyle,
    val bodyMediumRegular: TextStyle,
    val bodySmallSemiBold: TextStyle,
    val bodySmallMedium: TextStyle,
    val bodySmallRegular: TextStyle,
    val bodyExtraSmallSemiBold: TextStyle,
    val bodyExtraSmallMedium: TextStyle,
    val bodyExtraSmallRegular: TextStyle,
)

val invitationTypography =
    InvitationTypography(
        headingLarge =
            TextStyle(
                fontFamily = pretendardFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
            ).withLineHeightRatio(InvitationLineHeightRatio.HEADING),
        headingMedium =
            TextStyle(
                fontFamily = pretendardFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
            ).withLineHeightRatio(InvitationLineHeightRatio.HEADING),
        headingSmallBold =
            TextStyle(
                fontFamily = pretendardFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
            ).withLineHeightRatio(InvitationLineHeightRatio.HEADING),
        headingSmallSemiBold =
            TextStyle(
                fontFamily = pretendardFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
            ).withLineHeightRatio(InvitationLineHeightRatio.HEADING),
        bodyLargeSemiBold =
            TextStyle(
                fontFamily = pretendardFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
            ).withLineHeightRatio(InvitationLineHeightRatio.BODY),
        bodyLargeMedium =
            TextStyle(
                fontFamily = pretendardFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
            ).withLineHeightRatio(InvitationLineHeightRatio.BODY),
        bodyLargeRegular =
            TextStyle(
                fontFamily = pretendardFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
            ).withLineHeightRatio(InvitationLineHeightRatio.BODY),
        bodyMediumSemiBold =
            TextStyle(
                fontFamily = pretendardFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
            ).withLineHeightRatio(InvitationLineHeightRatio.BODY),
        bodyMediumMedium =
            TextStyle(
                fontFamily = pretendardFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
            ).withLineHeightRatio(InvitationLineHeightRatio.BODY),
        bodyMediumRegular =
            TextStyle(
                fontFamily = pretendardFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
            ).withLineHeightRatio(InvitationLineHeightRatio.BODY),
        bodySmallSemiBold =
            TextStyle(
                fontFamily = pretendardFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
            ).withLineHeightRatio(InvitationLineHeightRatio.BODY),
        bodySmallMedium =
            TextStyle(
                fontFamily = pretendardFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
            ).withLineHeightRatio(InvitationLineHeightRatio.BODY),
        bodySmallRegular =
            TextStyle(
                fontFamily = pretendardFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
            ).withLineHeightRatio(InvitationLineHeightRatio.BODY),
        bodyExtraSmallSemiBold =
            TextStyle(
                fontFamily = pretendardFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
            ).withLineHeightRatio(InvitationLineHeightRatio.BODY),
        bodyExtraSmallMedium =
            TextStyle(
                fontFamily = pretendardFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
            ).withLineHeightRatio(InvitationLineHeightRatio.BODY),
        bodyExtraSmallRegular =
            TextStyle(
                fontFamily = pretendardFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 11.sp,
            ).withLineHeightRatio(InvitationLineHeightRatio.BODY),
    )

internal val LocalInvitationTypography = staticCompositionLocalOf { invitationTypography }

@PreviewTheme
@Composable
private fun InvitationTypographyPreview() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
    ) {
        Text("Heading 1", style = InvitationTheme.typography.headingLarge)
        Text("Heading 2", style = InvitationTheme.typography.headingMedium)
        Text("Heading 3", style = InvitationTheme.typography.headingSmallBold)
        Text("Heading 4", style = InvitationTheme.typography.headingSmallSemiBold)
        Text("Body Large 1", style = InvitationTheme.typography.bodyLargeSemiBold)
        Text("Body Large 2", style = InvitationTheme.typography.bodyLargeMedium)
        Text("Body Large 3", style = InvitationTheme.typography.bodyLargeRegular)
        Text("Body Medium 1", style = InvitationTheme.typography.bodyMediumSemiBold)
        Text("Body Medium 2", style = InvitationTheme.typography.bodyMediumMedium)
        Text("Body Medium 3", style = InvitationTheme.typography.bodyMediumRegular)
        Text("Body Small 1", style = InvitationTheme.typography.bodySmallSemiBold)
        Text("Body Small 2", style = InvitationTheme.typography.bodySmallMedium)
        Text("Body Small 3", style = InvitationTheme.typography.bodySmallRegular)
        Text("Body Extra Small 1", style = InvitationTheme.typography.bodyExtraSmallSemiBold)
        Text("Body Extra Small 2", style = InvitationTheme.typography.bodyExtraSmallMedium)
        Text("Body Extra Small 3", style = InvitationTheme.typography.bodyExtraSmallRegular)
    }
}
