package com.andlife.designsystem.component.datepicker.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andlife.designsystem.component.datepicker.model.DatePickerDate
import com.andlife.designsystem.component.datepicker.model.DatePickerYearMonth
import com.andlife.designsystem.component.datepicker.state.DatePickerState
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

enum class DatePickerMode {
    DATE,  // 달력에서 날짜 선택
    YEAR_MONTH  // 연/월 선택
}

@Composable
fun DatePicker(
    state: DatePickerState,
    modifier: Modifier = Modifier
) {
    Crossfade(targetState = state.mode) { mode ->
        when (mode) {
            DatePickerMode.DATE -> {
                Column(modifier) {
                    DatePickerHeader(
                        title = "${state.displayedMonth.year}년 ${state.displayedMonth.month}월",
                        onHeaderClick = state::showYearMonthSelector,
                        onPreviousClick = state::moveToPreviousMonth,
                        onNextClick = state::moveToNextMonth
                    )
                    DatePickerCalendar(
                        yearMonth = state.displayedMonth,
                        selectedDate = state.selectedDate?.let { DatePickerDate(it) },
                        onDateClick = { date -> state.selectDate(date.date) }
                    )
                }
            }

            DatePickerMode.YEAR_MONTH -> {
                Column(modifier) {
                    DatePickerHeader(
                        title = "${state.displayedMonth.year}년",
                        onHeaderClick = {},
                        onPreviousClick = state::moveToPreviousYear,
                        onNextClick = state::moveToNextYear
                    )
                    DatePickerYearMonthSelector(
                        yearMonth = state.displayedMonth,
                        onMonthSelected = state::showCalendar
                    )
                }
            }
        }
    }
}

@Composable
private fun DatePickerHeader(
    title: String,
    onHeaderClick: () -> Unit = {},
    onPreviousClick: () -> Unit = {},
    onNextClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 이전 화살표
        Text(
            text = "◀",
            modifier = Modifier
                .clickable { onPreviousClick() }
                .padding(8.dp),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        // 제목 (클릭 가능)
        Text(
            text = title,
            modifier = Modifier.clickable { onHeaderClick() },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        // 다음 화살표
        Text(
            text = "▶",
            modifier = Modifier
                .clickable { onNextClick() }
                .padding(8.dp),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun DatePickerCalendar(
    yearMonth: DatePickerYearMonth,
    selectedDate: DatePickerDate?,
    onDateClick: (DatePickerDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val daysCountInMonth = getDaysCountInMonth(yearMonth)
    val firstDayOfWeek = getFirstDayOfWeek(yearMonth)

    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        // 요일 헤더
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("일", "월", "화", "수", "목", "금", "토").forEach { dayOfWeek ->
                Text(
                    text = dayOfWeek,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 날짜 그리드
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // 빈 공간 (월의 첫 번째 날 이전)
            items(firstDayOfWeek % 7) {
                Box(modifier = Modifier.size(40.dp))
            }

            // 실제 날짜들
            items(daysCountInMonth) { day ->
                val date = DatePickerDate(
                    LocalDate(yearMonth.year, yearMonth.month, day + 1)
                )
                val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
                val isSelected = selectedDate?.date == date.date
                val isToday = date.date == today
                val isBeforeToday = date.date < today

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isSelected -> MaterialTheme.colorScheme.primary
                                isToday -> Color.Gray.copy(alpha = 0.15f)
                                else -> Color.Transparent
                            }
                        )
                        .let { modifier ->
                            if (isBeforeToday) {
                                modifier
                            } else {
                                modifier.clickable { onDateClick(date) }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (day + 1).toString(),
                        color = when {
                            isBeforeToday -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            isSelected -> MaterialTheme.colorScheme.onPrimary
                            else -> MaterialTheme.colorScheme.onSurface
                        },
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun DatePickerYearMonthSelector(
    yearMonth: DatePickerYearMonth,
    onMonthSelected: (DatePickerYearMonth) -> Unit,
    modifier: Modifier = Modifier
) {
    val months = listOf(
        "1월", "2월", "3월", "4월",
        "5월", "6월", "7월", "8월",
        "9월", "10월", "11월", "12월"
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.SpaceAround,
        // horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.padding(16.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(12) { index ->
            val month = index + 1
            val isCurrentMonth = month == yearMonth.month

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(
                        if (isCurrentMonth) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surface
                    )
                    .clickable {
                        onMonthSelected(DatePickerYearMonth(yearMonth.year, month))
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = months[index],
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isCurrentMonth) FontWeight.Bold else FontWeight.Normal,
                    color = if (isCurrentMonth) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

// 해당 월의 첫째 날의 요일인덱스 반환: 일요일 = 0, 월요일 = 1, ...
private fun getFirstDayOfWeek(yearMonth: DatePickerYearMonth): Int {
    val date = LocalDate(yearMonth.year, yearMonth.month, 1)
    // DayOfWeek.ordinal: 월요일 = 0, 화요일 = 1, ...
    return (date.dayOfWeek.ordinal + 1) % 7
}

// 해당 월의 일수 반환
private fun getDaysCountInMonth(yearMonth: DatePickerYearMonth): Int {
    return when (yearMonth.month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (isLeapYear(yearMonth.year)) 29 else 28
        else -> 30
    }
}

// 윤년 여부 판단
private fun isLeapYear(year: Int): Boolean {
    return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
}
