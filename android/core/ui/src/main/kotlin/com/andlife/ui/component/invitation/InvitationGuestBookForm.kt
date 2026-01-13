package com.andlife.ui.component.invitation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.andlife.designsystem.component.InvitationButton
import com.andlife.designsystem.component.InvitationTextField
import com.andlife.designsystem.theme.InvitationIconSize
import com.andlife.designsystem.theme.InvitationSpacing
import com.andlife.designsystem.theme.InvitationTheme

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

        InvitationTextField(
            value = textContent,
            onValueChange = onTextContentChange,
            placeholder = "메시지를 남겨주세요.",
            modifier = Modifier.fillMaxWidth(),
            singleLine = false,
            minLines = 3,
        )

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
                    Text(text = "등록")
                }
            }
        }
    }
}
