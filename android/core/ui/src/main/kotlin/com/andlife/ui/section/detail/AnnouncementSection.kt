package com.andlife.ui.section.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.model.invitation.AnnouncementUiModel
import com.andlife.ui.R
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun AnnouncementSection(
    announcements: ImmutableList<AnnouncementUiModel>?,
    modifier: Modifier = Modifier,
) {
    if (announcements.isNullOrEmpty()) return

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(NachoTheme.colorScheme.backgroundPrimary)
                .padding(vertical = NachoSpacing.large, horizontal = NachoSpacing.medium),
        verticalArrangement = Arrangement.spacedBy(NachoSpacing.medium),
    ) {
        Text(
            text = stringResource(R.string.txt_announcement_title),
            style = NachoTheme.typography.headingSmallSemiBold,
            color = NachoTheme.colorScheme.textPrimary,
        )

        announcements.forEach { announcement ->
            Column(
                verticalArrangement = Arrangement.spacedBy(NachoSpacing.xSmall),
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = NachoTheme.shapes.small,
                    colors =
                        CardDefaults.cardColors(
                            containerColor = NachoTheme.colorScheme.backgroundSecondary,
                        ),
                ) {
                    Column(
                        modifier = Modifier.padding(NachoSpacing.large),
                        verticalArrangement = Arrangement.spacedBy(NachoSpacing.medium),
                    ) {
                        Text(
                            text = announcement.title,
                            style = NachoTheme.typography.bodyMediumSemiBold,
                            color = NachoTheme.colorScheme.textPrimary,
                        )

                        Text(
                            text = announcement.content,
                            style = NachoTheme.typography.bodyMediumRegular,
                            color = NachoTheme.colorScheme.textSecondary,
                        )
                    }
                }
            }
        }
    }
}

@PreviewTheme
@Composable
private fun AnnouncementSectionPreview() {
    NachoTheme {
        AnnouncementSection(
            announcements =
                persistentListOf(
                    AnnouncementUiModel(
                        title = "준비물",
                        content = " - 코딩할 수 있는 노트북 \n - 건강한 정신",
                    ),
                    AnnouncementUiModel(
                        title = "이벤트 안내",
                        content = "소정의 경품 행사가 있습니다~ 많이 많이 참석",
                    ),
                ),
        )
    }
}
