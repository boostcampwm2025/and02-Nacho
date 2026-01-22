package com.andlife.ui.component.listitem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoStroke
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.ui.component.loading.InvitationLoadingError
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.isUnspecified
import com.andlife.designsystem.preview.PreviewTheme

@Composable
fun InvitationScheduleListItemSkeleton(
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    onRetry: (() -> Unit)? = null,
) {
    val skeletonColor = NachoTheme.colorScheme.backgroundSecondary
    val cardColor = NachoTheme.colorScheme.backgroundPrimary
    val defaultShape = RoundedCornerShape(NachoSpacing.xSmall)

    val density = LocalDensity.current

    val titleHeight = with(density) {
        val lineHeight = NachoTheme.typography.bodyLargeSemiBold.lineHeight
        if (lineHeight.isUnspecified) 24.sp.toDp() else lineHeight.toDp()
    }
    val bodyHeight = with(density) {
        val lineHeight = NachoTheme.typography.bodyMediumRegular.lineHeight
        if (lineHeight.isUnspecified) 20.sp.toDp() else lineHeight.toDp()
    }

    Card(
        shape = NachoTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = BorderStroke(NachoStroke.small, NachoTheme.colorScheme.backgroundBorder),
        modifier = modifier.wrapContentHeight(),
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(skeletonColor),
                contentAlignment = Alignment.Center,
            ) {
                if (isError && onRetry != null) {
                    InvitationLoadingError(onRetry = onRetry)
                }
            }

            Column(
                modifier = Modifier.padding(NachoSpacing.medium),
                verticalArrangement = Arrangement.spacedBy(NachoSpacing.small),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(titleHeight)
                            .background(skeletonColor, defaultShape)
                    )

                    Spacer(Modifier.width(NachoSpacing.medium))

                    Box(
                        modifier = Modifier
                            .width(NachoSpacing.threeXLarge)
                            .height(titleHeight)
                            .background(skeletonColor, RoundedCornerShape(NachoSpacing.twoXLarge))
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(bodyHeight)
                        .background(skeletonColor, defaultShape)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(bodyHeight)
                        .background(skeletonColor, defaultShape)
                )
            }
        }
    }
}

@PreviewTheme
@Composable
private fun InvitationScheduleListItemSkeletonPreview() {
    NachoTheme {
        InvitationScheduleListItemSkeleton()
    }
}
