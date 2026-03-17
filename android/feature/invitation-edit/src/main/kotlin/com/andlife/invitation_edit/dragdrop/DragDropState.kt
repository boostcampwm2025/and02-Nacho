package com.andlife.invitation_edit.dragdrop

import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
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

@Stable
class DragDropState(
    private val lazyListState: LazyListState,
    private val onMove: (Int, Int) -> Unit
) {
    var draggingItemIndex by mutableStateOf<Int?>(null)
        private set

    var draggedDistance by mutableFloatStateOf(0f)
        private set

    private var draggingItemInitialInfo: LazyListItemInfo? = null

    fun onDragStart(index: Int, key: Any) {
        draggedDistance = 0f

        val itemInfo = lazyListState.layoutInfo.visibleItemsInfo.firstOrNull { it.key == key }

        if (itemInfo != null) {
            draggingItemIndex = itemInfo.index
            draggingItemInitialInfo = itemInfo
        }
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

            onMove(fromIndex, toIndex)

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
