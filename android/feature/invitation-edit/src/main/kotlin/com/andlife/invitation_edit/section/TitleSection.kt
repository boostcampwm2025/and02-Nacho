package com.andlife.invitation_edit.section

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import com.andlife.designsystem.component.NachoTextField
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.InvitationTheme
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.invitation_edit.R
import com.andlife.invitation_edit.component.FormLabel

@Composable
internal fun TitleSection(
    title: String,
    onTitleChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.background(NachoTheme.colorScheme.backgroundPrimary)) {
        Column(
            modifier = Modifier.padding(NachoSpacing.large),
            verticalArrangement = Arrangement.spacedBy(NachoSpacing.small),
        ) {
            FormLabel(title = stringResource(R.string.txt_title))
            NachoTextField(
                value = title,
                onValueChange = onTitleChange,
                placeholder = stringResource(R.string.desc_title),
                keyboardOptions =
                    KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next,
                    ),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
@PreviewTheme
private fun TitleSectionPreview() {
    InvitationTheme {
        TitleSection(
            title = "초대장 제목",
            onTitleChange = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
