package com.andlife.ui.component.invitation.collection

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoIconSize
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.ui.R

@Composable
fun StoryTextSection(
    content: String,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isTextOverflowing by remember { mutableStateOf(false) }

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .then(
                    if (isExpanded) {
                        Modifier.fillMaxSize()
                    } else {
                        Modifier.heightIn(min = NachoIconSize.huge)
                    },
                ).background(NachoTheme.colorScheme.backgroundOverlay)
                .clickable { if (isTextOverflowing || isExpanded) onToggleExpand() },
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .align(
                        if (isExpanded) {
                            Alignment.BottomStart
                        } else {
                            Alignment.TopStart
                        },
                    ).padding(NachoSpacing.large)
                    .animateContentSize(),
        ) {
            Text(
                text = content,
                style = NachoTheme.typography.bodyMediumSemiBold,
                color = NachoTheme.colorScheme.textOnPrimary,
                maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis,
                onTextLayout = { textLayoutResult ->
                    isTextOverflowing = textLayoutResult.lineCount > 2 ||
                        textLayoutResult.hasVisualOverflow
                },
            )

            if (isTextOverflowing) {
                Spacer(modifier = Modifier.height(NachoSpacing.large))
                Text(
                    text =
                        if (isExpanded) {
                            stringResource(
                                R.string.txt_collapse,
                            )
                        } else {
                            stringResource(R.string.txt_see_more)
                        },
                    style = NachoTheme.typography.bodyMediumSemiBold,
                    color = NachoTheme.colorScheme.textOnPrimary,
                    modifier = Modifier.clickable { onToggleExpand() },
                )
            }
        }
    }
}

@PreviewTheme
@Composable
private fun StoryTextSectionPreview() {
    NachoTheme {
        StoryTextSection(
            content =
                "계절이 지나가는 하늘에는 \n" +
                    "가을로 가득 차 있습니다. \n" +
                    "나는 아무 걱정도 없이 \n" +
                    "가을 속의 별들을 다 헤일 듯합니다. \n" +
                    "가슴속에 하나둘 새겨지는 별을 \n" +
                    "이제 다 못 헤는 것은 \n" +
                    "쉬이 아침이 오는 까닭이요, \n" +
                    "내일 밤이 남은 까닭이요, \n" +
                    "아직 나의 청춘이 다하지 않은 까닭입니다. \n" +
                    "별 하나에 추억과 별 하나에 사랑과 별 하나에 쓸쓸함과",
            isExpanded = true,
            onToggleExpand = {},
        )
    }
}
