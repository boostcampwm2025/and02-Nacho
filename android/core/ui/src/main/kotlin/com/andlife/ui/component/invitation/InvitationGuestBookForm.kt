package com.andlife.ui.component.invitation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.andlife.designsystem.component.InvitationButton
import com.andlife.designsystem.component.InvitationTextField
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.InvitationIconSize
import com.andlife.designsystem.theme.InvitationSpacing
import com.andlife.designsystem.theme.InvitationTheme
import com.andlife.ui.R

private const val MAX_LENGTH = 500

@Composable
fun InvitationGuestBookForm(
    selectedMedias: List<SelectedMedia>,
    textContent: String,
    isUploading: Boolean,
    onMediasSelected: (List<SelectedMedia>) -> Unit,
    onMediaRemove: (SelectedMedia) -> Unit,
    onTextContentChange: (String) -> Unit,
    onUploadClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        // 미디어 업로드 UI
        InvitationMediaUpload(
            selectedMedias = selectedMedias,
            onMediasSelected = onMediasSelected,
            onMediaRemove = onMediaRemove,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(InvitationSpacing.small))

        Box {
            InvitationTextField(
                value = textContent,
                onValueChange = { newValue ->
                    if (newValue.length <= MAX_LENGTH) {
                        onTextContentChange(newValue)
                    }
                },
                placeholder = stringResource(R.string.ph_please_leave_a_message),
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                minLines = 3,
            )

            Text(
                text = "${textContent.length}/$MAX_LENGTH",
                style = InvitationTheme.typography.bodySmallRegular,
                color = InvitationTheme.colorScheme.textTertiary,
                modifier =
                    Modifier
                        .align(Alignment.BottomEnd)
                        .padding(InvitationSpacing.small),
            )
        }

        Spacer(modifier = Modifier.height(InvitationSpacing.small))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            // 업로드 버튼
            InvitationButton(
                onClick = onUploadClick,
                enabled = (selectedMedias.isNotEmpty() || textContent.isNotEmpty()) && !isUploading,
            ) {
                if (isUploading) {
                    CircularProgressIndicator(
                        modifier = modifier.size(InvitationIconSize.small),
                        color = InvitationTheme.colorScheme.brandOnPrimary,
                    )
                } else {
                    Text(text = stringResource(R.string.txt_submit))
                }
            }
        }
    }
}

@PreviewTheme
@Composable
private fun InvitationGuestBookFormPreview() {
    InvitationTheme {
        InvitationGuestBookForm(
            selectedMedias = listOf(),
            textContent = "",
            isUploading = false,
            onMediasSelected = {},
            onMediaRemove = {},
            onTextContentChange = {},
            onUploadClick = {},
        )
    }
}
