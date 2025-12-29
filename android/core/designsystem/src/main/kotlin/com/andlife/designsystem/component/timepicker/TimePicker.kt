package com.andlife.designsystem.component.timepicker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andlife.designsystem.preview.ThemePreview
import com.andlife.designsystem.theme.InvitationSpacing
import com.andlife.designsystem.theme.InvitationTheme
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun InvitationTimePicker(
    state: InvitationTimePickerState,
    modifier: Modifier = Modifier,
    itemHeight: Dp = 50.dp
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AmPmColumn(
            isPm = state.isPm,
            itemHeight = itemHeight,
            onAmPmChange = { state.isPm = it },
            modifier = Modifier.weight(1f)
        )
        HourColumn(
            hour = state.hour,
            itemHeight = itemHeight,
            onHourChange = { newHour12 ->
                val oldHour12 = state.hour12
                if ((oldHour12 == 11 && newHour12 == 12) || (oldHour12 == 12 && newHour12 == 11)) {
                    state.isPm = !state.isPm
                }
                state.updateHour12(newHour12)
            },
            modifier = Modifier.weight(1f)
        )
        MinuteColumn(
            minute = state.minute,
            minuteInterval = state.minuteInterval,
            itemHeight = itemHeight,
            onMinuteChange = { state.minute = it },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun AmPmColumn(
    isPm: Boolean,
    itemHeight: Dp,
    onAmPmChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    BasicScrollableColumn(
        items = listOf("오전", "오후"),
        initialIndex = if (isPm) 1 else 0,
        itemHeight = itemHeight,
        onItemSelected = { onAmPmChange(it == 1) },
        isInfinite = false,
        modifier = modifier
    )
}

@Composable
fun HourColumn(
    hour: Int,
    itemHeight: Dp,
    onHourChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val hour12 = if (hour % 12 == 0) 12 else hour % 12
    BasicScrollableColumn(
        items = (1..12).map { it.toString() },
        initialIndex = hour12 - 1,
        itemHeight = itemHeight,
        onItemSelected = { index -> onHourChange(index + 1) },
        modifier = modifier
    )
}

@Composable
fun MinuteColumn(
    minute: Int,
    minuteInterval: Int,
    itemHeight: Dp,
    onMinuteChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = remember(minuteInterval) {
        (0 until 60 step minuteInterval).map { it.toString().padStart(2, '0') }
    }

    val initialIndex = remember(minute, minuteInterval) {
        (minute / minuteInterval).coerceIn(0, items.size - 1)
    }

    BasicScrollableColumn(
        items = items,
        initialIndex = initialIndex,
        itemHeight = itemHeight,
        onItemSelected = { index ->
            onMinuteChange(index * minuteInterval)
        },
        modifier = modifier
    )
}

@Composable
private fun BasicScrollableColumn(
    items: List<String>,
    initialIndex: Int,
    itemHeight: Dp,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    isInfinite: Boolean = true,
    isFadeEdgeEnabled: Boolean = false,
) {
    val itemCount = items.size
    val scope = rememberCoroutineScope()

    val repeatCount = if (isInfinite) 1000 else 1
    val totalItemCount = itemCount * repeatCount
    val startOffset = if (isInfinite) (repeatCount / 2) * itemCount else 0

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = startOffset + initialIndex
    )
    val snapBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    val haptic = LocalHapticFeedback.current

    var itemHeightPx by remember { mutableFloatStateOf(0f) }

//    val itemHeightDp = with(LocalDensity.current) {
//        if (itemHeightPx > 0f) {
//            itemHeightPx.toDp()
//        } else {
//            60.dp
//        }
//    }

    LaunchedEffect(initialIndex) {
        if (!listState.isScrollInProgress) {
            val currentFirstIndex = listState.firstVisibleItemIndex
            val currentRealIndex = currentFirstIndex % itemCount

            if (currentRealIndex != initialIndex) {
                val diff = initialIndex - currentRealIndex
                listState.animateScrollToItem(currentFirstIndex + diff)
            }
        }
    }

//    val currentIndex by remember {
//        derivedStateOf {
//            val centerItemIndex = listState.getCenterItemIndex() ?: return@derivedStateOf initialIndex
//            val realIndex = centerItemIndex % itemCount
//            realIndex
//        }
//    }

    val currentIndex by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            if (layoutInfo.visibleItemsInfo.isEmpty()) return@derivedStateOf initialIndex

            val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
            val centerItem = layoutInfo.visibleItemsInfo.minByOrNull {
                abs((it.offset + it.size / 2) - viewportCenter)
            }
            (centerItem?.index ?: 0) % itemCount
        }
    }

    LaunchedEffect(currentIndex) {
        onItemSelected(currentIndex)
    }

    var lastVibratedIndex by remember { mutableIntStateOf(-1) }
    LaunchedEffect(listState) {
        snapshotFlow { currentIndex }.collect { index ->
            if (listState.isScrollInProgress && lastVibratedIndex != index) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                lastVibratedIndex = index
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(itemHeight * 3)
            .then(
                if (isFadeEdgeEnabled) {
                    Modifier.fadeEdge(InvitationTheme.colorScheme.backgroundPrimary)
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            state = listState,
            flingBehavior = snapBehavior,
            contentPadding = PaddingValues(vertical = itemHeight),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            items(
                count = totalItemCount,
                key = { index ->
                    val realIndex = index % itemCount
                    "${items[realIndex]}_$index"
                }
            ) { index ->
                val realIndex = index % itemCount
                val itemAlpha by remember {
                    derivedStateOf {
                        val layoutInfo = listState.layoutInfo
                        val itemInfo = layoutInfo.visibleItemsInfo.find { it.index == index } ?: return@derivedStateOf 0.3f
                        val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
                        val distance = abs((itemInfo.offset + itemInfo.size / 2) - viewportCenter)
                        (1f - (distance.toFloat() / 100f)).coerceIn(0.3f, 1f)
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight)
                        .clickable {
                            scope.launch {
                                val currentFirstIndex = listState.firstVisibleItemIndex
                                val currentOffset = currentFirstIndex % itemCount
                                if (isInfinite) {
                                    var diff = realIndex - currentOffset
                                    if (abs(diff) > itemCount / 2) {
                                        if (diff > 0) diff -= itemCount
                                        else diff += itemCount
                                    }
                                    listState.animateScrollToItem(currentFirstIndex + diff)
                                } else {
                                    val diff = realIndex - currentOffset
                                    listState.animateScrollToItem(currentFirstIndex + diff)
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = items[realIndex],
                        fontSize = 20.sp,
                        modifier = Modifier.alpha(itemAlpha),
                        color = if (realIndex == currentIndex) Color.Black else Color.Gray
                    )
                }
            }
        }
    }
}

private fun LazyListState.getCenterItemIndex(): Int? {
    val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
    val centralItemInfo = layoutInfo.visibleItemsInfo.find { itemInfo ->
        val itemCenter = (itemInfo.offset + itemInfo.size / 2)
        itemCenter in (viewportCenter - itemInfo.size / 2)..(viewportCenter + itemInfo.size / 2)
    }
    return centralItemInfo?.index
}

@Composable
private fun Modifier.fadeEdge(color: Color): Modifier {
    val fadeEdgeBrushColor = remember(color) {
        arrayOf(
            0f to color.copy(alpha = 0.8f),
            0.20f to color.copy(alpha = 0.2f),
            0.5f to Color.Transparent,
            0.8f to color.copy(alpha = 0.2f),
            1f to color.copy(alpha = 0.8f),
        )
    }

    return this
        .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
        .drawWithCache {
            val brush = Brush.verticalGradient(
                colorStops = fadeEdgeBrushColor,
                startY = 0f,
                endY = size.height,
            )
            onDrawWithContent {
                drawContent()
                drawRect(
                    brush = brush,
                    blendMode = BlendMode.DstOut,
                )
            }
        }
}

@ThemePreview
@Composable
private fun InvitationTimePickerPreview() {
    InvitationTheme {
        val state = rememberInvitationTimePickerState(
            initialHour = 10,
            initialMinute = 30,
            minuteInterval = 5,
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            InvitationTimePicker(
                state = state,
                modifier = Modifier
                    .fillMaxWidth()
            )

            Text(
                text = "선택된 시간: ${state.hour12}시 ${state.minute}분 ${if (state.isPm) "오후" else "오전"}",
                style = InvitationTheme.typography.bodyLargeMedium,
                modifier = Modifier
                    .padding(top = InvitationSpacing.large)
            )
        }
    }
}