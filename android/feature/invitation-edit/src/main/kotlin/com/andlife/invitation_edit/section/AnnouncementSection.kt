package com.andlife.invitation_edit.section

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.andlife.designsystem.component.NachoButton
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoElevation
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.invitation_edit.R
import com.andlife.invitation_edit.dragdrop.DragDropState
import com.andlife.invitation_edit.dragdrop.dragDropItem
import com.andlife.invitation_edit.model.form.AnnouncementUiModel
import kotlinx.collections.immutable.ImmutableList
import com.andlife.designsystem.R as designR

fun LazyListScope.announcementSection(
    announcementList: ImmutableList<AnnouncementUiModel>,
    onRemoveAnnouncementClick: (AnnouncementUiModel) -> Unit,
    onAddAnnouncementClick: () -> Unit,
    onEditAnnouncementClick: (AnnouncementUiModel) -> Unit,
    dragDropState: DragDropState,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
) {
    item {
        Box(modifier = modifier.background(NachoTheme.colorScheme.backgroundPrimary)) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = NachoSpacing.large),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.txt_announcement),
                    style = NachoTheme.typography.bodyMediumSemiBold,
                    color = NachoTheme.colorScheme.textPrimary,
                )

                NachoButton(
                    onClick = onAddAnnouncementClick,
                    enabled = !isLoading,
                    elevation =
                        ButtonDefaults.buttonElevation(
                            defaultElevation = NachoElevation.none,
                            pressedElevation = NachoElevation.none,
                        ),
                    containerColor = NachoTheme.colorScheme.brandOnPrimary,
                    contentColor = NachoTheme.colorScheme.brandPrimary,
                    contentPadding = PaddingValues(horizontal = NachoSpacing.small, vertical = NachoSpacing.xSmall),
                    disabledContainerColor = Color.Transparent,
                    disabledContentColor = NachoTheme.colorScheme.textTertiary
                ) {
                    Icon(
                        painter = painterResource(id = designR.drawable.ic_add_24),
                        contentDescription = stringResource(R.string.desc_add_announcement),
                    )
                    Spacer(Modifier.width(NachoSpacing.small))
                    Text(
                        text = stringResource(R.string.txt_add_announcement),
                        style = NachoTheme.typography.bodyMediumMedium,
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
                        .background(NachoTheme.colorScheme.backgroundPrimary),
            ) {
                Text(
                    text = stringResource(R.string.desc_add_announcement),
                    style = NachoTheme.typography.bodyMediumSemiBold,
                    color = NachoTheme.colorScheme.textTertiary,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = NachoSpacing.threeXLarge),
                    textAlign = TextAlign.Center,
                )
            }
        }
    } else {
        itemsIndexed(
            items = announcementList,
            key = { _, item -> item.id },
        ) { index, item ->
            val absoluteIndex = index + 8

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NachoTheme.colorScheme.backgroundPrimary)
                    .dragDropItem(absoluteIndex, dragDropState)
                    .clickable { onEditAnnouncementClick(item) }
            ) {
                AnnouncementItem(
                    announcementUiModel = item,
                    onRemoveClick = { onRemoveAnnouncementClick(item) },
                    onDragStart = { dragDropState.onDragStart(absoluteIndex, item.id) },
                    onDrag = { offset -> dragDropState.onDrag(offset) },
                    onDragInterrupted = { dragDropState.onDragInterrupted() }
                )
            }
        }
    }
}

@Composable
private fun AnnouncementItem(
    announcementUiModel: AnnouncementUiModel,
    onRemoveClick: () -> Unit,
    onDragStart: () -> Unit,
    onDrag: (Offset) -> Unit,
    onDragInterrupted: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = NachoTheme.shapes.small,
    color: Color = NachoTheme.colorScheme.backgroundSecondary,
) {
    Surface(
        modifier =
            modifier.padding(
                horizontal = NachoSpacing.large,
                vertical = NachoSpacing.small,
            ),
        shape = shape,
        color = color,
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = NachoSpacing.large, vertical = NachoSpacing.medium)
                    .height(IntrinsicSize.Min),
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(NachoSpacing.medium),
            ) {
                Text(
                    text = announcementUiModel.title,
                    style = NachoTheme.typography.bodyLargeMedium,
                    color = NachoTheme.colorScheme.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = announcementUiModel.content,
                    style = NachoTheme.typography.bodyMediumMedium,
                    color = NachoTheme.colorScheme.textSecondary,
                )
            }
            Column(modifier = Modifier) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_reorder),
                    contentDescription = null,
                    tint = NachoTheme.colorScheme.textTertiary,
                    modifier = Modifier.pointerInput(Unit) {
                        detectDragGesturesAfterLongPress(
                            onDragStart = { onDragStart() },
                            onDrag = { change, dragAmount ->
                                change.consume() // 이벤트 독점
                                onDrag(dragAmount)
                            },
                            onDragEnd = { onDragInterrupted() },
                            onDragCancel = { onDragInterrupted() }
                        )
                    }
                )
                Spacer(
                    modifier =
                        Modifier
                            .heightIn(min = NachoSpacing.xLarge)
                            .weight(1f),
                )
                Icon(
                    modifier =
                        Modifier
                            .clip(CircleShape)
                            .clickable(
                                role = Role.Button,
                                onClick = onRemoveClick,
                            ),
                    imageVector = ImageVector.vectorResource(R.drawable.ic_trash),
                    contentDescription = null,
                    tint = NachoTheme.colorScheme.textTertiary,
                )
            }
        }
    }
}

@Composable
@PreviewTheme
private fun AnnouncementSectionPreview() {
    NachoTheme {
    }
}
