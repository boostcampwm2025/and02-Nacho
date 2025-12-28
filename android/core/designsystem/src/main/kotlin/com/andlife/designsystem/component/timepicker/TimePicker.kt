package com.andlife.designsystem.component.timepicker

import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andlife.designsystem.preview.ThemePreview
import com.andlife.designsystem.theme.InvitationTheme

@Composable
fun InvitationTimePicker(
    state: InvitationTimePickerState,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicScrollableColumn(
            items = listOf("오전", "오후"),
            modifier = Modifier.weight(1f)
        )
        BasicScrollableColumn(
            items = (0..12).map { it.toString() },
            modifier = Modifier.weight(1f)
        )
        BasicScrollableColumn(
            items = (0..59 step state.minuteInterval).map { it.toString().padStart(2, '0') },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun BasicScrollableColumn(
    items: List<String>,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val snapBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    LazyColumn(
        modifier = modifier.height(200.dp),
        state = listState,
        flingBehavior = snapBehavior,
        contentPadding = PaddingValues(vertical = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items(
            items = items,
            key = { it }
        ) { item ->
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = item)
            }
        }
    }
}

private fun LazyListState.getCentralItemIndex(): Int? {
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

        InvitationTimePicker(
            state = state,
        )
    }
}