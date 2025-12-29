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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.andlife.designsystem.preview.ThemePreview
import com.andlife.designsystem.theme.InvitationSpacing
import com.andlife.designsystem.theme.InvitationTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun InvitationTimePicker(
    state: InvitationTimePickerState,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = InvitationTheme.typography.headingLarge,
    itemVerticalPadding: Dp = InvitationSpacing.threeXLarge,
    isFadeEdgeEnabled: Boolean = false,
) {
    val density = LocalDensity.current
    val itemHeight = remember(textStyle, itemVerticalPadding) {
        with(density) {
            val fontSizeDp = textStyle.fontSize.toDp()
            fontSizeDp + (itemVerticalPadding * 2)
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = InvitationSpacing.large),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AmPmColumn(
            isPm = state.isPm,
            onAmPmChange = { state.isPm = it },
            itemHeight = itemHeight,
            textStyle = textStyle,
            isFadeEdgeEnabled = isFadeEdgeEnabled,
            modifier = Modifier.weight(1f)
        )
        HourColumn(
            hour12 = state.hour12,
            onHourChange = { newHour12 ->
                val oldHour12 = state.hour12
                if ((oldHour12 == 11 && newHour12 == 12) || (oldHour12 == 12 && newHour12 == 11)) {
                    state.isPm = !state.isPm
                }
                state.updateHour12(newHour12)
            },
            itemHeight = itemHeight,
            textStyle = textStyle,
            isFadeEdgeEnabled = isFadeEdgeEnabled,
            modifier = Modifier.weight(1f)
        )
        MinuteColumn(
            minute = state.minute,
            minuteInterval = state.minuteInterval,
            onMinuteChange = { state.minute = it },
            itemHeight = itemHeight,
            textStyle = textStyle,
            isFadeEdgeEnabled = isFadeEdgeEnabled,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun AmPmColumn(
    isPm: Boolean,
    itemHeight: Dp,
    textStyle: TextStyle,
    isFadeEdgeEnabled: Boolean = false,
    onAmPmChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    BasicScrollableColumn(
        items = listOf("오전", "오후").toImmutableList(), // TODO: 리소스화
        initialIndex = if (isPm) 1 else 0,
        externalSelectedIndex = if (isPm) 1 else 0,
        onItemSelected = { onAmPmChange(it == 1) },
        itemHeight = itemHeight,
        textStyle = textStyle,
        isInfinite = false,
        isFadeEdgeEnabled = isFadeEdgeEnabled,
        modifier = modifier
    )
}

@Composable
fun HourColumn(
    hour12: Int,
    onHourChange: (Int) -> Unit,
    itemHeight: Dp,
    textStyle: TextStyle,
    isFadeEdgeEnabled: Boolean,
    modifier: Modifier
) {
    BasicScrollableColumn(
        items = (1..12).map { it.toString() }.toImmutableList(),
        initialIndex = hour12 - 1,
        externalSelectedIndex = hour12 - 1,
        onItemSelected = { onHourChange(it + 1) },
        itemHeight = itemHeight,
        textStyle = textStyle,
        isInfinite = true,
        isFadeEdgeEnabled = isFadeEdgeEnabled,
        modifier = modifier
    )
}

@Composable
fun MinuteColumn(
    minute: Int,
    minuteInterval: Int,
    onMinuteChange: (Int) -> Unit,
    itemHeight: Dp,
    textStyle: TextStyle,
    isFadeEdgeEnabled: Boolean,
    modifier: Modifier
) {
    val items = remember(minuteInterval) {
        (0 until 60 step minuteInterval).map { it.toString().padStart(2, '0') }
    }
    val currentIndex = (minute / minuteInterval).coerceIn(0, items.size - 1)

    BasicScrollableColumn(
        items = items.toImmutableList(),
        initialIndex = currentIndex,
        externalSelectedIndex = currentIndex,
        onItemSelected = { onMinuteChange(it * minuteInterval) },
        itemHeight = itemHeight,
        textStyle = textStyle,
        isInfinite = true,
        isFadeEdgeEnabled = isFadeEdgeEnabled,
        modifier = modifier
    )
}

@Composable
private fun BasicScrollableColumn(
    items: ImmutableList<String>,
    initialIndex: Int,
    externalSelectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    itemHeight: Dp,
    textStyle: TextStyle,
    isFadeEdgeEnabled: Boolean,
    modifier: Modifier = Modifier,
    isInfinite: Boolean = true,
) {
    val itemCount = items.size
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val repeatCount = if (isInfinite) 1000 else 1
    val startOffset = if (isInfinite) (repeatCount / 2) * itemCount else 0

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = startOffset + initialIndex
    )
    val snapBehavior = rememberSnapFlingBehavior(lazyListState = listState)

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

    LaunchedEffect(externalSelectedIndex) {
        if (!listState.isScrollInProgress && currentIndex != externalSelectedIndex) {
            val currentPosition = listState.firstVisibleItemIndex
            val currentRealIndex = currentPosition % itemCount
            val diff = externalSelectedIndex - currentRealIndex
            val scrollTarget = if (isInfinite) {
                val adjustedDiff = when {
                    abs(diff) <= itemCount / 2 -> diff
                    diff > 0 -> diff - itemCount
                    else -> diff + itemCount
                }
                currentPosition + adjustedDiff
            } else {
                externalSelectedIndex
            }
            listState.animateScrollToItem(scrollTarget)
        }
    }

    var lastVibratedIndex by remember { mutableIntStateOf(-1) }
    LaunchedEffect(listState) {
        snapshotFlow { currentIndex }
            .collect { index ->
                if (lastVibratedIndex != index) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    lastVibratedIndex = index
                    onItemSelected(index)
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
            modifier = Modifier.fillMaxWidth(),
            state = listState,
            flingBehavior = snapBehavior,
            contentPadding = PaddingValues(vertical = itemHeight),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            items(
                count = itemCount * repeatCount,
                key = { index -> "${index}_${items[index % itemCount]}" }
            ) { index ->
                val realIndex = index % itemCount
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight)
                        .graphicsLayer {
                            val layoutInfo = listState.layoutInfo
                            val itemInfo = layoutInfo.visibleItemsInfo.find { it.index == index }
                            if (itemInfo != null) {
                                val viewportCenter =
                                    (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2f
                                val itemCenter = itemInfo.offset + itemInfo.size / 2f
                                val distance = abs(itemCenter - viewportCenter)
                                alpha = (1f - (distance / size.height)).coerceIn(0.3f, 1f)
                            } else {
                                alpha = 0.3f
                            }
                        }
                        .clickable {
                            scope.launch {
                                val currentFirstIndex = listState.firstVisibleItemIndex
                                val currentRealIndex = currentFirstIndex % itemCount
                                var diff = realIndex - currentRealIndex
                                if (isInfinite && abs(diff) > itemCount / 2) {
                                    diff = if (diff > 0) diff - itemCount else diff + itemCount
                                }
                                listState.animateScrollToItem(currentFirstIndex + diff)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = items[realIndex],
                        style = textStyle,
                        color = if (realIndex == currentIndex) {
                            InvitationTheme.colorScheme.textPrimary
                        } else {
                            InvitationTheme.colorScheme.textTertiary
                        },
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun Modifier.fadeEdge(color: Color): Modifier {
    val fadeEdgeBrushColor = remember(color) {
        arrayOf(
            0f to color.copy(alpha = 0.9f),
            0.25f to color.copy(alpha = 0.2f),
            0.5f to Color.Transparent,
            0.75f to color.copy(alpha = 0.2f),
            1f to color.copy(alpha = 0.9f),
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
                .padding(InvitationSpacing.large),
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