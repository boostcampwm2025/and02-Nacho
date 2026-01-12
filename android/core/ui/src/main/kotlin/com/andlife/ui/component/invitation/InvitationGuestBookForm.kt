package com.andlife.ui.component.invitation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.andlife.designsystem.component.InvitationButton
import com.andlife.designsystem.theme.InvitationSpacing

@Composable
fun InvitationGuestBookForm(
    selectedMedias: List<SelectedMedia>,
    isUploading: Boolean,
    onMediasSelected: (List<SelectedMedia>) -> Unit,
    onMediaRemove: (SelectedMedia) -> Unit,
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

        Spacer(modifier = Modifier.height(InvitationSpacing.large))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            // 업로드 버튼
            InvitationButton(
                onClick = onUploadClick,
                enabled = selectedMedias.isNotEmpty() && !isUploading,
            ) {
                if (isUploading) {
                    CircularProgressIndicator()
                } else {
                    Text(text = "등록")
                }
            }
        }

        // 업로드된 미디어 URL 표시
//        if (uploadedMediaUrls.isNotEmpty()) {
//            Text(text = "업로드된 미디어:")
//            uploadedMediaUrls.forEach { url ->
//                Text(text = url)
//            }
//        }
    }
}
