package com.andlife.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andlife.designsystem.R
import com.andlife.designsystem.theme.InvitationIconSize
import com.andlife.designsystem.theme.InvitationSpacing
import com.andlife.designsystem.theme.InvitationTheme

@Composable
fun InvitationButton(
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
){
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = InvitationTheme.shapes.small,
        colors = ButtonDefaults.buttonColors(
            containerColor = InvitationTheme.colorScheme.brandPrimary,
            contentColor = InvitationTheme.colorScheme.brandOnPrimary,
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp,
            disabledElevation = 0.dp
        ),
        contentPadding = PaddingValues(
            horizontal = InvitationSpacing.medium,
            vertical = InvitationSpacing.medium
        )
    ) {
        Text(
            text = text,
            style = InvitationTheme.typography.bodyLarge1
        )
    }
}

@Composable
fun InvitationIconButton(
    iconRes: Int,
    enabled: Boolean= true,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
){
    Surface(
        modifier = modifier,
        shape = InvitationTheme.shapes.small,
        color = if (enabled) InvitationTheme.colorScheme.brandPrimary
        else InvitationTheme.colorScheme.backgroundBorder,
        contentColor = InvitationTheme.colorScheme.brandOnPrimary,
    ) {
        IconButton(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier.size(InvitationIconSize.large)
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = contentDescription,
                modifier = Modifier.size(InvitationIconSize.medium)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InvitationButtonPreview(){
    InvitationTheme {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                InvitationButton(
                    text = "초대 생성하기",
                    enabled = true,
                    onClick = {},
                    modifier = Modifier.weight(1f),
                )
            }

            InvitationButton(
                text = "저장",
                enabled = true,
                onClick = {},
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InvitationIconButtonPreview() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(InvitationSpacing.xSmall)
    ) {
        InvitationTheme {
            InvitationIconButton(
                iconRes = R.drawable.ic_search_24,
                onClick = {},
                contentDescription = "검색"
            )
        }
        InvitationTheme {
            InvitationIconButton(
                iconRes = R.drawable.ic_search_24,
                enabled = false,
                onClick = {},
                contentDescription = "검색"
            )
        }
    }
}