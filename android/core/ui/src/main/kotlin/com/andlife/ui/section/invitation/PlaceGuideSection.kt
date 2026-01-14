package com.andlife.ui.section.invitation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.andlife.model.invitation.LocationInfo
import com.andlife.ui.R

@Composable
fun PlaceGuideSection(
    location: LocationInfo,
    modifier: Modifier = Modifier,
) {
    val hasGuide = location.guide.isNotBlank()

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(NachoTheme.colorScheme.backgroundPrimary)
                .padding(vertical = NachoSpacing.large, horizontal = NachoSpacing.medium),
        verticalArrangement = Arrangement.spacedBy(NachoSpacing.medium),
    ) {
        Text(
            text = stringResource(R.string.txt_place_guide),
            style = NachoTheme.typography.headingSmallSemiBold,
            color = NachoTheme.colorScheme.textPrimary,
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(NachoSpacing.small),
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = NachoTheme.shapes.small,
                colors =
                    CardDefaults.cardColors(
                        containerColor = NachoTheme.colorScheme.backgroundSecondary,
                    ),
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .background(color = NachoTheme.colorScheme.backgroundSecondary)
                            .padding(vertical = NachoSpacing.threeXLarge),
                    contentAlignment = Alignment.Center,
                ) {
                    // TODO: NaverMap 구현
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(NachoSpacing.medium),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_map_32),
                            contentDescription = null,
                            tint = NachoTheme.colorScheme.textTertiary,
                            modifier = Modifier.padding(NachoSpacing.small),
                        )
                        Text(
                            text = stringResource(R.string.txt_map_loading),
                            style = NachoTheme.typography.bodyMediumMedium,
                            color = NachoTheme.colorScheme.textTertiary,
                        )
                    }
                }
            }

            if (hasGuide) {
                Text(
                    text = location.guide,
                    style = NachoTheme.typography.bodyMediumRegular,
                    color = NachoTheme.colorScheme.textSecondary,
                )
            }
        }
    }
}

@PreviewTheme
@Composable
private fun PlaceGuideSectionPreview() {
    NachoTheme {
        PlaceGuideSection(
            location = LocationInfo(),
        )
    }
}
