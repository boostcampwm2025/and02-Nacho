package com.andlife.invitation_edit.section

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.InvitationSpacing
import com.andlife.designsystem.theme.InvitationTheme
import com.andlife.invitation_edit.R
import com.andlife.invitation_edit.model.create.AnnouncementUiModel
import kotlinx.collections.immutable.ImmutableList

fun LazyListScope.announcementSection(
    announcementList: ImmutableList<AnnouncementUiModel>,
    onRemoveAnnouncementClick: (AnnouncementUiModel) -> Unit,
    onAddAnnouncementClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    item {
        Box(modifier = modifier.background(InvitationTheme.colorScheme.backgroundPrimary)) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = InvitationSpacing.large)
                        .padding(top = InvitationSpacing.large),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.txt_announcement),
                    style = InvitationTheme.typography.bodyMediumSemiBold,
                    color = InvitationTheme.colorScheme.textPrimary,
                )
                TextButton(onAddAnnouncementClick) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.desc_add_announcement),
                        tint = InvitationTheme.colorScheme.brandPrimary,
                    )
                    Text(
                        text = stringResource(R.string.txt_add_announcement),
                        style = InvitationTheme.typography.bodyMediumMedium,
                        color = InvitationTheme.colorScheme.brandPrimary,
                    )
                }
            }
        }
    }
    if (announcementList.isEmpty()) {
        item {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(InvitationTheme.colorScheme.backgroundPrimary),
            ) {
                Text(
                    text = stringResource(R.string.desc_add_announcement),
                    style = InvitationTheme.typography.bodyMediumSemiBold,
                    color = InvitationTheme.colorScheme.textTertiary,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = InvitationSpacing.threeXLarge),
                    textAlign = TextAlign.Center,
                )
            }
        }
    } else {
        itemsIndexed(
            items = announcementList,
            key = { _, item -> item.id },
        ) { index, item ->
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(InvitationTheme.colorScheme.backgroundPrimary),
            ) {
                AnnouncementItem(
                    announcementUiModel = item,
                    onRemoveClick = { onRemoveAnnouncementClick(item) },
                )
            }
        }
    }
}

@Composable
private fun AnnouncementItem(
    announcementUiModel: AnnouncementUiModel,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = InvitationTheme.shapes.small,
    color: Color = InvitationTheme.colorScheme.backgroundSecondary,
) {
    Surface(
        modifier =
            modifier.padding(
                horizontal = InvitationSpacing.large,
                vertical = InvitationSpacing.small,
            ),
        shape = shape,
        color = color,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = InvitationSpacing.large, vertical = InvitationSpacing.medium)
                .height(IntrinsicSize.Min),
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(InvitationSpacing.medium),
            ) {
                Text(
                    text = announcementUiModel.title,
                    style = InvitationTheme.typography.bodyLargeMedium,
                    color = InvitationTheme.colorScheme.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = announcementUiModel.content,
                    style = InvitationTheme.typography.bodyMediumMedium,
                    color = InvitationTheme.colorScheme.textSecondary,
                )
            }
            Column(modifier = Modifier) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_reorder),
                    contentDescription = null,
                    tint = InvitationTheme.colorScheme.textTertiary
                )
                Spacer(
                    modifier = Modifier
                        .heightIn(min = InvitationSpacing.xLarge)
                        .weight(1f)
                )
                Icon(
                    modifier = Modifier
                        .clickable(
                            role = Role.Button,
                            onClick = onRemoveClick
                        ),
                    imageVector = ImageVector.vectorResource(R.drawable.ic_trash),
                    contentDescription = null,
                    tint = InvitationTheme.colorScheme.textTertiary
                )
            }
        }
    }
}

@Composable
@PreviewTheme
private fun AnnouncementSectionPreview() {
    InvitationTheme {
    }
}
