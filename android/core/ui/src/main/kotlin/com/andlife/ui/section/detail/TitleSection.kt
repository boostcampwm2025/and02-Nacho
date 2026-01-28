package com.andlife.ui.section.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.ui.R

@Composable
fun TitleSection(
    title: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(NachoTheme.colorScheme.backgroundPrimary)
                .padding(vertical = NachoSpacing.xLarge, horizontal = NachoSpacing.large),
    ) {
        Text(
            text = title,
            style = NachoTheme.typography.headingLarge,
            color = NachoTheme.colorScheme.textPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun EmptyTitleSection(
    placeholderText: String = stringResource(R.string.txt_title_placeholder),
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(NachoTheme.colorScheme.backgroundPrimary)
                .padding(vertical = NachoSpacing.xLarge, horizontal = NachoSpacing.large),
    ) {
        Text(
            text = placeholderText,
            style = NachoTheme.typography.headingLarge,
            color = NachoTheme.colorScheme.textTertiary,
            modifier = Modifier.alpha(0.6f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@PreviewTheme
@Composable
private fun TitleSectionPreview() {
    NachoTheme {
        TitleSection(
            title = "네부캠 송년회",
        )
    }
}

@PreviewTheme
@Composable
private fun EmptyTitleSectionPreview() {
    NachoTheme {
        EmptyTitleSection()
    }
}
