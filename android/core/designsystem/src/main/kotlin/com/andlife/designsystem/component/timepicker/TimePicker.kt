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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.andlife.designsystem.preview.ThemePreview
import com.andlife.designsystem.theme.InvitationSpacing
import com.andlife.designsystem.theme.InvitationTheme
import kotlinx.coroutines.launch

@Composable
fun InvitationTimePicker(
    state: InvitationTimePickerState,
    modifier: Modifier = Modifier,
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
            onAmPmChange = { state.isPm = it },
            modifier = Modifier.weight(1f)
        )
        HourColumn(
            hour = state.hour,
            onHourChange = { newHour12 ->
                val newHour12 = newHour12
                val oldHour12 = if (state.hour % 12 == 0) 12 else state.hour % 12

                if ((oldHour12 == 11 && newHour12 == 12) || (oldHour12 == 12 && newHour12 == 11)) {
                    state.isPm = !state.isPm
                }

                state.hour = when {
                    state.isPm && newHour12 != 12 -> newHour12 + 12
                    !state.isPm && newHour12 == 12 -> 0
                    else -> newHour12
                }
            },
            modifier = Modifier.weight(1f)
        )
        MinuteColumn(
            minute = state.minute,
            onMinuteChange = { state.minute = it },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun AmPmColumn(
    isPm: Boolean,
    onAmPmChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    BasicScrollableColumn(
        items = listOf("오전", "오후"),
        initialIndex = if (isPm) 1 else 0,
        onItemSelected = { onAmPmChange(it == 1) },
        isInfinite = false,
        modifier = modifier
    )
}

@Composable
fun HourColumn(
    hour: Int,
    onHourChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val hour12 = if (hour % 12 == 0) 12 else hour % 12
    BasicScrollableColumn(
        items = (1..12).map { it.toString() },
        initialIndex = hour12 - 1,
        onItemSelected = { index -> onHourChange(index + 1) },
        modifier = modifier
    )
}

@Composable
fun MinuteColumn(
    minute: Int,
    onMinuteChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    BasicScrollableColumn(
        items = (0..59).map { it.toString().padStart(2, '0') },
        initialIndex = minute,
        onItemSelected = { onMinuteChange(it) },
        modifier = modifier
    )
}

@Composable
private fun BasicScrollableColumn(
    items: List<String>,
    initialIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    isInfinite: Boolean = true,
) {
    val itemHeight = 60.dp // TODO: 나중에 외부에서 받도록 변경
    val itemCount = items.size

    val scope = rememberCoroutineScope()

    val repeatCount = if (isInfinite) 1000 else 1
    val totalItemCount = itemCount * repeatCount
    val startOffset = if (isInfinite) (repeatCount / 2) * itemCount else 0

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = startOffset + initialIndex
    )
    val snapBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    val currentIndex = listState.firstVisibleItemIndex % itemCount

    LaunchedEffect(currentIndex) {
        onItemSelected(currentIndex)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(itemHeight * 3),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight)
                .padding(horizontal = InvitationSpacing.large),
            color = InvitationTheme.colorScheme.backgroundBorder,
            shape = InvitationTheme.shapes.small
        ) {

        }
        LazyColumn(
            state = listState,
            flingBehavior = snapBehavior,
            contentPadding = PaddingValues(vertical = itemHeight),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            items(
                count = totalItemCount,
                key = { index -> index }
            ) { index ->
                val realIndex = index % itemCount
                val isSelected = realIndex == currentIndex
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight)
                        .clickable {
                            scope.launch {
                                if (isInfinite) {
                                    val currentFirstIndex = listState.firstVisibleItemIndex
                                    val currentOffset = currentFirstIndex % itemCount
                                    val diff = realIndex - currentOffset

                                    listState.animateScrollToItem(currentFirstIndex + diff)
                                } else {
                                    listState.animateScrollToItem(index)
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = items[realIndex],
                        color = if (isSelected) InvitationTheme.colorScheme.textPrimary else InvitationTheme.colorScheme.textTertiary,
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