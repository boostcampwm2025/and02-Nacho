package com.andlife.ui.component.invitation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.andlife.designsystem.theme.NachoIconSize
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoStroke
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.ui.R
import kotlinx.collections.immutable.ImmutableList
import com.andlife.designsystem.R as designR

@Composable
fun InvitationListHeader(
    totalCount: Int,
    currentSort: String,
    sortOptions: ImmutableList<String>,
    onSortSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.format_total_invitation_count, totalCount),
            style = NachoTheme.typography.headingSmallSemiBold,
            color = NachoTheme.colorScheme.textPrimary
        )

        Box {
            Surface(
                onClick = { isMenuExpanded = true },
                shape = NachoTheme.shapes.extraLarge,
                color = NachoTheme.colorScheme.backgroundSecondary,
                border = BorderStroke(NachoStroke.small, NachoTheme.colorScheme.backgroundBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = NachoSpacing.medium, vertical = NachoSpacing.small),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(NachoSpacing.xSmall)
                ) {
                    Text(
                        text = currentSort,
                        style = NachoTheme.typography.bodyMediumMedium,
                        color = NachoTheme.colorScheme.textPrimary
                    )
                    Icon(
                        painter = painterResource(designR.drawable.ic_arrow_drop_down_24),
                        contentDescription = null,
                        modifier = Modifier.size(NachoIconSize.small),
                        tint = NachoTheme.colorScheme.textPrimary
                    )
                }
            }

            DropdownMenu(
                expanded = isMenuExpanded,
                onDismissRequest = { isMenuExpanded = false },
                shape = NachoTheme.shapes.medium,
                containerColor = NachoTheme.colorScheme.backgroundPrimary,
                modifier = Modifier
                    .background(Color.Transparent)
            ) {
                sortOptions.forEachIndexed { index, option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                style = NachoTheme.typography.bodyMediumMedium,
                                color = if (currentSort == option) NachoTheme.colorScheme.brandDark
                                else NachoTheme.colorScheme.textPrimary
                            )
                        },
                        onClick = {
                            onSortSelected(index)
                            isMenuExpanded = false
                        }
                    )

                    if (index < sortOptions.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = NachoSpacing.small),
                            thickness = NachoStroke.small,
                            color = NachoTheme.colorScheme.backgroundBorder
                        )
                    }
                }
            }
        }
    }
}
