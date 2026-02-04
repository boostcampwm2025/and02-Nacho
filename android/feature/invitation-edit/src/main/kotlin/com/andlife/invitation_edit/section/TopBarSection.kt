package com.andlife.invitation_edit.section

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.andlife.designsystem.component.NachoButton
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoElevation
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.invitation_edit.R
import com.andlife.designsystem.R as designR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TopBarSection(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onPreviewClick: (() -> Unit)? = null,
    isLoading: Boolean = false,
    isPreviewMode: Boolean = false,
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = NachoTheme.typography.headingSmallSemiBold,
                color = NachoTheme.colorScheme.textPrimary,
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onBackClick,
                enabled = !isLoading,
            ) {
                Icon(
                    painter = painterResource(designR.drawable.ic_arrow_back_24),
                    contentDescription = stringResource(R.string.desc_back),
                    tint = NachoTheme.colorScheme.iconSecondary,
                )
            }
        },
        actions = {
            if (onPreviewClick != null && !isPreviewMode) {
                NachoButton(
                    modifier = Modifier.padding(end = NachoSpacing.medium),
                    onClick = onPreviewClick,
                    enabled = !isLoading,
                    contentPadding = PaddingValues(
                        horizontal = NachoSpacing.medium,
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = NachoElevation.none,
                        pressedElevation = NachoElevation.none,
                    ),
                    containerColor = NachoTheme.colorScheme.brandOnPrimary,
                    contentColor = NachoTheme.colorScheme.brandPrimary,
                    disabledContainerColor = Color.Transparent,
                    disabledContentColor = NachoTheme.colorScheme.textTertiary
                ) {
                    Text(
                        text = stringResource(R.string.txt_preview),
                        style = NachoTheme.typography.bodyLargeSemiBold,
                    )
                }
            }
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = NachoTheme.colorScheme.backgroundPrimary,
            ),
        modifier = modifier,
    )
}

@Composable
@PreviewTheme
private fun TopBarSectionPreview() {
    NachoTheme {
        TopBarSection(
            title = "초대 생성",
            onBackClick = {},
            onPreviewClick = {},
        )
    }
}
