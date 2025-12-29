package com.andlife.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.andlife.designsystem.R
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.InvitationElevation
import com.andlife.designsystem.theme.InvitationTheme

@Composable
fun InvitationButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = InvitationTheme.shapes.small,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    elevation: ButtonElevation =
        ButtonDefaults.buttonElevation(
            defaultElevation = InvitationElevation.small,
            pressedElevation = InvitationElevation.medium,
        ),
    containerColor: Color = InvitationTheme.colorScheme.brandPrimary,
    contentColor: Color = InvitationTheme.colorScheme.brandOnPrimary,
    content: @Composable RowScope.() -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = containerColor,
                contentColor = contentColor,
            ),
        elevation = elevation,
        contentPadding = contentPadding,
    ) {
        content()
    }
}

@PreviewTheme
@Composable
private fun InvitationButtonPreview() {
    InvitationTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            InvitationButton(
                onClick = {},
            ) {
                Text("저장")
            }

            InvitationButton(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("초대 생성하기")
            }

            InvitationButton(
                onClick = {},
                elevation =
                    ButtonDefaults.buttonElevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp,
                    ),
                containerColor = InvitationTheme.colorScheme.brandOnPrimary,
                contentColor = InvitationTheme.colorScheme.brandPrimary,
            ) {
                Text("미리보기")
            }

            InvitationButton(
                onClick = {},
                elevation =
                    ButtonDefaults.buttonElevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp,
                    ),
                containerColor = InvitationTheme.colorScheme.brandOnPrimary,
                contentColor = InvitationTheme.colorScheme.brandPrimary,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search_24),
                    contentDescription = "검색",
                )
                Spacer(Modifier.width(8.dp))
                Text("초대카드 편집")
            }

            InvitationButton(
                onClick = {},
                enabled = false,
            ) {
                Text("비활성화")
            }
        }
    }
}
