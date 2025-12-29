package com.andlife.designsystem.component.datepicker.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import com.andlife.designsystem.theme.InvitationTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.andlife.designsystem.R
import com.andlife.designsystem.component.datepicker.model.DatePickerDate
import com.andlife.designsystem.component.datepicker.model.DatePickerYearMonth
import com.andlife.designsystem.component.datepicker.state.DatePickerState
import com.andlife.designsystem.theme.InvitationSpacing
import kotlinx.datetime.LocalDate

enum class DatePickerMode {
    DATE,  // 달력에서 날짜 선택
    YEAR_MONTH  // 연/월 선택
}

@Composable
fun DatePicker(
    state: DatePickerState,
    modifier: Modifier = Modifier,
    colors: DatePickerColors = DatePickerDefaults.colors()
) {
    Crossfade(targetState = state.mode) { mode ->
        when (mode) {
            DatePickerMode.DATE -> {
                Column(modifier) {
                    DatePickerHeader(
                        title = "${state.displayedMonth.year}년 ${state.displayedMonth.month}월",
                        onHeaderClick = state::showYearMonthSelector,
                        onPreviousClick = state::moveToPreviousMonth,
                        onNextClick = state::moveToNextMonth,
                        colors = colors,
                        isClickable = true,
                    )
                    DatePickerCalendar(
                        yearMonth = state.displayedMonth,
                        selectedDate = state.selectedDate?.let { DatePickerDate(it) },
                        onDateClick = { date -> state.selectDate(date.date) },
                        colors = colors
                    )
                }
            }

            DatePickerMode.YEAR_MONTH -> {
                Column(modifier) {
                    DatePickerHeader(
                        title = "${state.displayedMonth.year}년",
                        onHeaderClick = {},
                        onPreviousClick = state::moveToPreviousYear,
                        onNextClick = state::moveToNextYear,
                        colors = colors
                    )
                    DatePickerYearMonthSelector(
                        yearMonth = state.displayedMonth,
                        onMonthSelected = state::showCalendar,
                        colors = colors
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
    colors: DatePickerColors,
    modifier: Modifier = Modifier,
    isClickable: Boolean = false
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(InvitationSpacing.large),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_chevron_left_24),
            contentDescription = null,
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onPreviousClick() }
                .padding(InvitationSpacing.small),
            tint = colors.navigationColor
        )

        // 제목 (클릭 가능)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .let { modifier ->
                    if (isClickable) {
                        modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onHeaderClick() }
                    } else {
                        modifier
                    }
                }
                .padding(InvitationSpacing.small)
        ) {
            Text(
                text = title,
                style = InvitationTheme.typography.headingMedium,
                color = colors.headerTextColor
            )
            if (isClickable)
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_drop_down_24),
                    contentDescription = null,
                    tint = colors.navigationColor
                )
        }

        Icon(
            painter = painterResource(id = R.drawable.ic_chevron_right_24),
            contentDescription = null,
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onNextClick() }
                .padding(InvitationSpacing.small),
            tint = colors.navigationColor
        )
    }
}

@Composable
private fun DatePickerCalendar(
    yearMonth: DatePickerYearMonth,
    selectedDate: DatePickerDate?,
    onDateClick: (DatePickerDate) -> Unit,
    colors: DatePickerColors,
    modifier: Modifier = Modifier
) {
    val daysCountInMonth = getDaysCountInMonth(yearMonth)
    val firstDayOfWeek = getFirstDayOfWeek(yearMonth)

    Column(modifier = modifier.padding(horizontal = InvitationSpacing.large)) {
        // 요일 헤더
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("일", "월", "화", "수", "목", "금", "토").forEach { dayOfWeek ->
                Text(
                    text = dayOfWeek,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = InvitationTheme.typography.bodyMediumSemiBold,
                    color = colors.weekdayTextColor
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 날짜 그리드
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            verticalArrangement = Arrangement.spacedBy(InvitationSpacing.medium),
            modifier = Modifier.fillMaxWidth(),
        ) {
            // 빈 공간 (월의 첫 번째 날 이전)
            items(firstDayOfWeek % 7) {
                Box(modifier = Modifier.size(28.dp))
            }

            // 실제 날짜들
            items(daysCountInMonth) { day ->
                val date = DatePickerDate(
                    LocalDate(yearMonth.year, yearMonth.month, day + 1)
                )
                val isSelected = selectedDate?.date == date.date
                val isToday = date.date == DatePickerDefaults.today()
                val isBeforeToday = date.date < DatePickerDefaults.today()

                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .let { modifier ->
                            if (isBeforeToday) modifier
                            else modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onDateClick(date)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(0.7f) // 원 크기 조절
                            .clip(CircleShape)
                            .background(
                                when {
                                    isSelected -> colors.selectedDateColor
                                    isToday -> colors.todayBackgroundColor
                                    else -> Color.Transparent
                                }
                            )
                    )

                    Text(
                        text = (day + 1).toString(),
                        style = if (isSelected || isToday) InvitationTheme.typography.bodyMediumMedium else InvitationTheme.typography.bodyMediumRegular,
                        color = when {
                            isBeforeToday -> colors.disabledTextColor
                            isSelected -> colors.selectedTextColor
                            isToday -> colors.todayTextColor
                            else -> colors.normalTextColor
                        },
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
    colors: DatePickerColors,
    modifier: Modifier = Modifier
) {
    val months = listOf(
        "1월", "2월", "3월", "4월",
        "5월", "6월", "7월", "8월",
        "9월", "10월", "11월", "12월"
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(InvitationSpacing.medium),
        modifier = modifier.padding(),
        contentPadding = PaddingValues(horizontal = InvitationSpacing.large)
    ) {
        items(12) { index ->
            val month = index + 1
            val isCurrentMonth = month == yearMonth.month

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clip(InvitationTheme.shapes.small)
                    .background(
                        if (isCurrentMonth) colors.selectedMonthColor
                        else Color.Transparent
                    )
                    .clickable {
                        onMonthSelected(DatePickerYearMonth(yearMonth.year, month))
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = months[index],
                    style = if (isCurrentMonth) InvitationTheme.typography.bodyMediumSemiBold else InvitationTheme.typography.bodyMediumRegular,
                    color = if (isCurrentMonth) colors.selectedTextColor
                    else colors.normalTextColor
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
