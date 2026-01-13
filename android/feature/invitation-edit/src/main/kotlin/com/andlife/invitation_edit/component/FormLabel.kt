package com.andlife.invitation_edit.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.InvitationTheme
import com.andlife.designsystem.theme.NachoTheme

@Composable
internal fun FormLabel(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text =
            buildAnnotatedString {
                append(title)
                append(" ")
                withStyle(style = SpanStyle(color = NachoTheme.colorScheme.brandPrimary)) {
                    append("*")
                }
            },
        style = NachoTheme.typography.bodyMediumSemiBold,
        color = NachoTheme.colorScheme.textPrimary,
        modifier = modifier,
    )
}

@Composable
@PreviewTheme
private fun FormLabelPreview() {
    InvitationTheme {
        FormLabel("초대 제목")
    }
}
