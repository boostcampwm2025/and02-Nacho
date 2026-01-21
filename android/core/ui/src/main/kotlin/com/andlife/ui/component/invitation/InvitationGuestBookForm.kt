package com.andlife.ui.component.invitation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.andlife.designsystem.component.NachoButton
import com.andlife.designsystem.component.NachoTextField
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoIconSize
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.ui.R
import com.andlife.ui.util.media.uriToSelectedMedia
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

private const val MAX_LENGTH = 500
private const val MAX_MEDIAS_COUNT = 5

@Composable
fun InvitationGuestBookForm(
    selectedMedias: ImmutableList<SelectedMedia>,
    textContent: String,
    isUploading: Boolean,
    isSubmittable: Boolean,
    onMediasSelected: (ImmutableList<SelectedMedia>) -> Unit,
    onMediaRemove: (SelectedMedia) -> Unit,
    onTextContentChange: (String) -> Unit,
    onUploadClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetMultipleContents(),
        ) { uris ->
            val uriStrings = uris.map { it.toString() }
            val availableSlots = MAX_MEDIAS_COUNT - selectedMedias.size

            if (availableSlots <= 0) return@rememberLauncherForActivityResult

            val mediasToAdd =
                uriStrings
                    .take(availableSlots)
                    .map { uriToSelectedMedia(context, it) }

            onMediasSelected((selectedMedias + mediasToAdd).toImmutableList())
        }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(NachoSpacing.small),
    ) {
        // 미디어 업로드 UI
        InvitationMediaUpload(
            selectedMedias = selectedMedias,
            onMediaRemove = onMediaRemove,
            modifier = Modifier.fillMaxWidth(),
        )

        Box {
            NachoTextField(
                value = textContent,
                onValueChange = { newValue ->
                    if (newValue.length <= MAX_LENGTH) {
                        onTextContentChange(newValue)
                    }
                },
                placeholder = stringResource(R.string.txt_please_leave_a_message),
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                minLines = 3,
            )

            Text(
                text = "${textContent.length}/$MAX_LENGTH",
                style = NachoTheme.typography.bodySmallRegular,
                color = NachoTheme.colorScheme.textTertiary,
                modifier =
                    Modifier
                        .align(Alignment.BottomEnd)
                        .padding(NachoSpacing.small),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // 미디어 아이콘 표시
            val isMediaAddEnabled = selectedMedias.size < MAX_MEDIAS_COUNT
            val iconColor =
                if (isMediaAddEnabled) {
                    NachoTheme.colorScheme.brandPrimary
                } else {
                    NachoTheme.colorScheme.iconDisabled
                }
            Row(horizontalArrangement = Arrangement.spacedBy(NachoSpacing.small)) {
                Icon(
                    painter = painterResource(R.drawable.ic_image_16),
                    contentDescription = null,
                    tint = iconColor,
                    modifier =
                        Modifier
                            .size(NachoIconSize.medium)
                            .let {
                                if (isMediaAddEnabled) {
                                    it.clickable {
                                        launcher.launch("image/*")
                                    }
                                } else {
                                    it
                                }
                            },
                )
                Icon(
                    painter = painterResource(R.drawable.ic_camera_16),
                    contentDescription = null,
                    tint = iconColor,
                    modifier =
                        Modifier
                            .size(NachoIconSize.medium)
                            .clickable {
                                // TODO: 카메라 촬영 기능 추가
                            },
                )
                Icon(
                    painter = painterResource(R.drawable.ic_file_16),
                    contentDescription = null,
                    tint = iconColor,
                    modifier =
                        Modifier
                            .size(NachoIconSize.medium)
                            .clickable {
                                launcher.launch("*/*")
                            },
                )
                Icon(
                    painter = painterResource(R.drawable.ic_mic_16),
                    contentDescription = null,
                    tint = iconColor,
                    modifier =
                        Modifier
                            .size(NachoIconSize.medium)
                            .clickable {
                                // TODO: 마이크 녹음 기능 추가
                            },
                )
            }
            NachoButton(
                onClick = onUploadClick,
                enabled = isSubmittable,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.txt_submit),
                        color = if (isUploading) Color.Transparent else Color.Unspecified
                    )
                    if (isUploading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(NachoIconSize.small),
                            color = NachoTheme.colorScheme.brandOnPrimary,
                        )
                    }
                }
            }
        }
    }
}

@PreviewTheme
@Composable
private fun InvitationGuestBookFormPreview() {
    NachoTheme {
        InvitationGuestBookForm(
            selectedMedias = persistentListOf(),
            textContent = "",
            isUploading = false,
            isSubmittable = false,
            onMediasSelected = {},
            onMediaRemove = {},
            onTextContentChange = {},
            onUploadClick = {},
        )
    }
}
