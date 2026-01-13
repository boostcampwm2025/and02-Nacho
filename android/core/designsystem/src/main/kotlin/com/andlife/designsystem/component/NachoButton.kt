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
import com.andlife.designsystem.theme.NachoElevation
import com.andlife.designsystem.theme.InvitationTheme
import com.andlife.designsystem.theme.NachoTheme

@Composable
fun NachoButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = NachoTheme.shapes.small,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    elevation: ButtonElevation =
        ButtonDefaults.buttonElevation(
            defaultElevation = NachoElevation.small,
            pressedElevation = NachoElevation.medium,
        ),
    containerColor: Color = NachoTheme.colorScheme.brandPrimary,
    contentColor: Color = NachoTheme.colorScheme.brandOnPrimary,
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
            NachoButton(
                onClick = {},
            ) {
                Text("저장")
            }

            NachoButton(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("초대 생성하기")
            }

            NachoButton(
                onClick = {},
                elevation =
                    ButtonDefaults.buttonElevation(
                        defaultElevation = NachoElevation.none,
                        pressedElevation = NachoElevation.none,
                    ),
                containerColor = NachoTheme.colorScheme.brandOnPrimary,
                contentColor = NachoTheme.colorScheme.brandPrimary,
            ) {
                Text("미리보기")
            }

            NachoButton(
                onClick = {},
                elevation =
                    ButtonDefaults.buttonElevation(
                        defaultElevation = NachoElevation.none,
                        pressedElevation = NachoElevation.none,
                    ),
                containerColor = NachoTheme.colorScheme.brandOnPrimary,
                contentColor = NachoTheme.colorScheme.brandPrimary,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_chevron_down_16),
                    contentDescription = "검색",
                )
                Spacer(Modifier.width(8.dp))
                Text("초대카드 편집")
            }

            NachoButton(
                onClick = {},
                enabled = false,
            ) {
                Text("비활성화")
            }
        }
    }
}
