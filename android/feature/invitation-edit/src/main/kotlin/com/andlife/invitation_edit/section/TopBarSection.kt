package com.andlife.invitation_edit.section

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.invitation_edit.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TopBarSection(
    title: String,
    onBackClick: () -> Unit,
    onPreviewClick: () -> Unit,
    modifier: Modifier = Modifier,
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
            IconButton(onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = stringResource(R.string.desc_back),
                )
            }
        },
        actions = {
            TextButton(onPreviewClick) {
                Text(
                    text = stringResource(R.string.txt_preview),
                    style = NachoTheme.typography.bodyLargeSemiBold,
                    color = NachoTheme.colorScheme.brandPrimary,
                )
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
