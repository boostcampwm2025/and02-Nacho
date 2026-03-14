package com.andlife.invitation_edit.dragdrop

import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.zIndex

@Composable
fun rememberDragDropState(
    lazyListState: LazyListState,
    onMove: (Int, Int) -> Unit
): DragDropState {
    return remember(lazyListState) {
        DragDropState(lazyListState, onMove)
    }
}

class DragDropState(
    private val lazyListState: LazyListState,
    private val onMove: (Int, Int) -> Unit
) {
    var draggingItemIndex by mutableStateOf<Int?>(null)
        private set

    var draggedDistance by mutableFloatStateOf(0f)
        private set

    private var draggingItemInitialInfo: LazyListItemInfo? = null

    fun onDragStart(index: Int) {
        draggingItemIndex = index
        draggingItemInitialInfo = lazyListState.layoutInfo.visibleItemsInfo.firstOrNull { it.index == index }
    }

    fun onDrag(offset: Offset) {
        draggedDistance += offset.y
        val initialInfo = draggingItemInitialInfo ?: return
        val currentDraggingIndex = draggingItemIndex ?: return

        val currentStart = initialInfo.offset + draggedDistance
        val currentEnd = currentStart + initialInfo.size

        val overlappingItem = lazyListState.layoutInfo.visibleItemsInfo.firstOrNull { item ->
            if (item.index == currentDraggingIndex || item.index < 8) return@firstOrNull false

            val itemCenter = item.offset + (item.size / 2)

            if (draggedDistance > 0) {
                currentEnd > itemCenter && currentDraggingIndex < item.index
            } else {
                currentStart < itemCenter && currentDraggingIndex > item.index
            }
        }

        if (overlappingItem != null) {
            val fromIndex = currentDraggingIndex
            val toIndex = overlappingItem.index

            val distanceCorrection = initialInfo.offset - overlappingItem.offset


            draggingItemIndex = toIndex
            draggedDistance += distanceCorrection
            draggingItemInitialInfo = overlappingItem
        }
    }

    fun onDragInterrupted() {
        draggingItemIndex = null
        draggedDistance = 0f
        draggingItemInitialInfo = null
    }
}

fun Modifier.dragDropItem(
    index: Int,
    state: DragDropState
): Modifier {
    val isDragging = state.draggingItemIndex == index

    return this
        .zIndex(if (isDragging) 100f else 1f)
        .graphicsLayer {
            translationY = if (isDragging) state.draggedDistance else 0f

            val scale = if (isDragging) 1.05f else 1f
            scaleX = scale
            scaleY = scale

            shadowElevation = if (isDragging) 10f else 0f
            alpha = if (isDragging) 0.95f else 1f
        }
}
