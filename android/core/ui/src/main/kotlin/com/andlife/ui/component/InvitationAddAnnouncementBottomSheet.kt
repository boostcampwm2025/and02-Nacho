package com.andlife.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.andlife.designsystem.component.InvitationButton
import com.andlife.designsystem.component.InvitationTextField
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.InvitationElevation
import com.andlife.designsystem.theme.InvitationSpacing
import com.andlife.designsystem.theme.InvitationTheme
import com.andlife.ui.R

@Composable
fun TitleAndTextField(
    title: String,
    textFieldValue: String,
    onTextFieldValueChange: (String) -> Unit,
    textFieldPlaceholder: String,
    modifier: Modifier = Modifier,
    minLines: Int = 1,
) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = modifier,
    ) {
        Text(
            text = title,
            style = InvitationTheme.typography.bodyMediumSemiBold,
            color = InvitationTheme.colorScheme.textPrimary,
        )
        Spacer(modifier = Modifier.height(InvitationSpacing.small))
        InvitationTextField(
            value = textFieldValue,
            onValueChange = onTextFieldValueChange,
            placeholder = textFieldPlaceholder,
            modifier = Modifier.fillMaxWidth(),
            minLines = minLines,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvitationAddAnnouncementBottomSheet(
    onConfirm: (String, String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState =
        rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
        )

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = { },
        sheetState = sheetState,
        containerColor = InvitationTheme.colorScheme.backgroundPrimary,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = InvitationTheme.colorScheme.textSecondary,
            )
        },
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = "공지사항 추가",
                style = InvitationTheme.typography.headingSmallSemiBold,
                color = InvitationTheme.colorScheme.textPrimary,
                modifier = Modifier.align(Alignment.Center),
            )

            Box(
                modifier =
                    Modifier
                        .align(Alignment.CenterEnd)
                        .padding(horizontal = InvitationSpacing.large)
                        .clickable { onDismiss() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close_24),
                    contentDescription = "닫기",
                    tint = InvitationTheme.colorScheme.textPrimary,
                )
            }
        }
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = InvitationSpacing.large)
                    .padding(bottom = InvitationSpacing.large),
        ) {
            Spacer(modifier = Modifier.height(InvitationSpacing.large))

            TitleAndTextField(
                title = "공지사항 제목",
                textFieldValue = title,
                onTextFieldValueChange = { title = it },
                textFieldPlaceholder = "제목을 입력해주세요",
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(InvitationSpacing.medium))

            TitleAndTextField(
                title = "공지사항 내용",
                textFieldValue = content,
                onTextFieldValueChange = { content = it },
                textFieldPlaceholder = "내용을 입력해주세요",
                modifier = Modifier.fillMaxWidth(),
                minLines = 5,
            )

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(
                    onClick = {
                        title = ""
                        content = ""
                    },
                    contentPadding = PaddingValues(vertical = InvitationSpacing.medium),
                ) {
                    Text(
                        text = "모두 지우기",
                        color = InvitationTheme.colorScheme.iconPrimary,
                        style = InvitationTheme.typography.bodyMediumSemiBold,
                    )
                }
            }

            Spacer(modifier = Modifier.height(InvitationSpacing.medium))

            InvitationButton(
                onClick = {
                    onConfirm(title, content)
                },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = InvitationSpacing.medium),
                elevation =
                    ButtonDefaults.buttonElevation(
                        defaultElevation = InvitationElevation.none,
                        pressedElevation = InvitationElevation.none,
                    ),
            ) {
                Text(
                    text = "등록",
                    style = InvitationTheme.typography.bodyLargeSemiBold,
                )
            }
        }
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
