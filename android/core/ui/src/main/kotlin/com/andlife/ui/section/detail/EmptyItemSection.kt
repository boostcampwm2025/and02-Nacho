package com.andlife.ui.section.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.ui.R

@Composable
fun EmptyItemSection(
    title: String,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(NachoTheme.colorScheme.backgroundPrimary)
                .padding(vertical = NachoSpacing.large, horizontal = NachoSpacing.medium),
        verticalArrangement = Arrangement.spacedBy(NachoSpacing.medium),
    ) {
        Text(
            text = title,
            style = NachoTheme.typography.headingSmallSemiBold,
            color = NachoTheme.colorScheme.textPrimary,
        )
        Row(
            modifier =
                modifier
                    .fillMaxWidth()
                    .background(
                        color = NachoTheme.colorScheme.backgroundSecondary,
                        shape = NachoTheme.shapes.small
                    )
                    .padding(NachoSpacing.threeXLarge),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(NachoSpacing.medium),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_info_24),
                contentDescription = stringResource(R.string.desc_info),
                tint = NachoTheme.colorScheme.textTertiary,
            )
            Text(
                text = placeholder,
                style = NachoTheme.typography.bodyMediumRegular,
                color = NachoTheme.colorScheme.textTertiary,
            )
        }
    }
}

@PreviewTheme
@Composable
private fun EmptyItemSectionPreview() {
    EmptyItemSection(
        title = "공지사항",
        placeholder = "공지사항을 입력해주세요",
    )
}
