package com.andlife.invitation_edit.section

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import com.andlife.designsystem.component.NachoTextField
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.InvitationTheme
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.invitation_edit.R
import com.andlife.invitation_edit.component.FormLabel

@Composable
internal fun AuthorSection(
    authorName: String,
    onAuthorNameChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    Box(modifier = modifier.background(NachoTheme.colorScheme.backgroundPrimary)) {
        Column(
            modifier = Modifier.padding(NachoSpacing.large),
            verticalArrangement = Arrangement.spacedBy(NachoSpacing.small),
        ) {
            Row {
                FormLabel(title = stringResource(R.string.txt_author_name))
            }
            NachoTextField(
                value = authorName,
                onValueChange = onAuthorNameChange,
                placeholder = stringResource(R.string.desc_author),
                keyboardActions =
                    KeyboardActions(
                        onDone = { focusManager.clearFocus() },
                    ),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
@PreviewTheme
private fun AuthorSectionPreview() {
    InvitationTheme {
        AuthorSection(
            authorName = "주최자 이름",
            onAuthorNameChange = {},
        )
    }
}
