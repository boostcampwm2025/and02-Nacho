package com.andlife.ui.component.addannouncement

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.andlife.designsystem.component.InvitationButton
import com.andlife.designsystem.component.InvitationTextField
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.InvitationElevation
import com.andlife.designsystem.theme.InvitationSpacing
import com.andlife.designsystem.theme.InvitationTheme
import com.andlife.ui.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvitationAddAnnouncementBottomSheet(
    onConfirm: (String, String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InvitationAddAnnouncementViewModel = viewModel(),
) {
    val keyboardManager = LocalSoftwareKeyboardController.current
    val sheetState =
        rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
        )

    val scope = rememberCoroutineScope()
    val draft by viewModel.announcementDraft.collectAsStateWithLifecycle()

    ModalBottomSheet(
        onDismissRequest = { }, // 사용하지 않음
        sheetState = sheetState,
        containerColor = InvitationTheme.colorScheme.backgroundPrimary,
        dragHandle = null,
        sheetGesturesEnabled = false,
        properties =
            ModalBottomSheetProperties(
                shouldDismissOnBackPress = false,
                shouldDismissOnClickOutside = false,
            ),
        modifier = modifier,
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = InvitationSpacing.twoXLarge),
        ) {
            Text(
                text = stringResource(R.string.label_add_announcement),
                style = InvitationTheme.typography.headingSmallSemiBold,
                color = InvitationTheme.colorScheme.textPrimary,
                modifier = Modifier.align(Alignment.Center),
            )

            Box(
                modifier =
                    Modifier
                        .align(Alignment.CenterEnd)
                        .padding(horizontal = InvitationSpacing.large)
                        .clickable {
                            scope
                                .launch {
                                    keyboardManager?.hide()
                                    sheetState.hide()
                                }.invokeOnCompletion { onDismiss() }
                        },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close_24),
                    contentDescription = stringResource(R.string.desc_close),
                    tint = InvitationTheme.colorScheme.textPrimary,
                )
            }
        }
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(all = InvitationSpacing.large),
            verticalArrangement = Arrangement.spacedBy(InvitationSpacing.medium),
        ) {
            TitleAndTextField(
                title = stringResource(R.string.label_announcement_title),
                textFieldValue = draft.title,
                onTextFieldValueChange = viewModel::updateTitle,
                textFieldPlaceholder = stringResource(R.string.tf_announcement_title_hint),
                modifier = Modifier.fillMaxWidth(),
            )

            TitleAndTextField(
                title = stringResource(R.string.label_announcement_content),
                textFieldValue = draft.content,
                onTextFieldValueChange = viewModel::updateContent,
                textFieldPlaceholder = stringResource(R.string.tf_announcement_content_hint),
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                minLines = 5,
            )

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                Text(
                    text = stringResource(R.string.txt_delete_all),
                    modifier =
                        Modifier
                            .clickable {
                                viewModel.clearDraft()
                            },
                    color = InvitationTheme.colorScheme.iconPrimary,
                    style = InvitationTheme.typography.bodyMediumSemiBold,
                )
            }

            InvitationButton(
                onClick = {
                    onConfirm(draft.title, draft.content)
                    viewModel.clearDraft()
                    scope
                        .launch {
                            keyboardManager?.hide()
                            sheetState.hide()
                        }.invokeOnCompletion { onDismiss() }
                },
                enabled = draft.title.isNotBlank() && draft.content.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = InvitationSpacing.medium),
                elevation =
                    ButtonDefaults.buttonElevation(
                        defaultElevation = InvitationElevation.none,
                        pressedElevation = InvitationElevation.none,
                    ),
            ) {
                Text(
                    text = stringResource(R.string.txt_submit),
                    style = InvitationTheme.typography.bodyLargeSemiBold,
                )
            }
        }
    }
}

@Composable
fun TitleAndTextField(
    title: String,
    textFieldValue: String,
    onTextFieldValueChange: (String) -> Unit,
    textFieldPlaceholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    minLines: Int = 1,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(InvitationSpacing.small),
        horizontalAlignment = Alignment.Start,
        modifier = modifier,
    ) {
        Text(
            text = title,
            style = InvitationTheme.typography.bodyMediumSemiBold,
            color = InvitationTheme.colorScheme.textPrimary,
        )
        InvitationTextField(
            value = textFieldValue,
            onValueChange = onTextFieldValueChange,
            placeholder = textFieldPlaceholder,
            modifier = Modifier.fillMaxWidth(),
            minLines = minLines,
            singleLine = singleLine,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewTheme
@Composable
private fun InvitationAddAnnouncementBottomSheetPreview() {
    InvitationTheme {
        InvitationAddAnnouncementBottomSheet(
            onConfirm = { _, _ -> },
            onDismiss = {},
        )
    }
}
