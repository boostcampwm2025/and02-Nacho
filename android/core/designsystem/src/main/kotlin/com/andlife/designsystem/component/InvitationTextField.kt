package com.andlife.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.andlife.designsystem.R
import com.andlife.designsystem.preview.ThemePreview
import com.andlife.designsystem.theme.InvitationSpacing
import com.andlife.designsystem.theme.InvitationTheme

@Composable
fun InvitationTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    trailingIcon: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    singleLine: Boolean = true,
    minLines: Int = 1,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                style = InvitationTheme.typography.bodyMediumSemiBold
            )
        },
        trailingIcon = trailingIcon,
        isError = isError,
        enabled = enabled,
        readOnly = readOnly,
        singleLine = singleLine,
        minLines = if (singleLine) 1 else minLines,
        textStyle = InvitationTheme.typography.bodyMediumSemiBold,
        shape = InvitationTheme.shapes.extraSmall,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = InvitationTheme.colorScheme.textPrimary,
            unfocusedTextColor = InvitationTheme.colorScheme.textPrimary,
            focusedPlaceholderColor = InvitationTheme.colorScheme.textTertiary,
            unfocusedPlaceholderColor = InvitationTheme.colorScheme.textTertiary,
            unfocusedBorderColor = InvitationTheme.colorScheme.backgroundBorder,
            focusedBorderColor = InvitationTheme.colorScheme.brandPrimary,
        ),
        modifier = modifier.fillMaxWidth()
    )

}

@ThemePreview
@Composable
private fun InvitationTextFieldPreview() {
    InvitationTheme {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(InvitationSpacing.small)
        ) {
            var address = ""

            InvitationTextField(
                value = address,
                onValueChange = { address = it },
                placeholder = "주소를 검색해주세요",
                trailingIcon = {
                    IconButton(onClick = { }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_search_24),
                            contentDescription = "검색",
                            tint = InvitationTheme.colorScheme.iconPrimary
                        )
                    }
                }
            )

            InvitationTextField(
                value = "",
                onValueChange = { address = it },
                placeholder = "공지사항 추가",
                singleLine = false,
                minLines = 10
            )
        }
    }
}