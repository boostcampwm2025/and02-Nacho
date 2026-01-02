package com.andlife.designsystem.component.datepicker.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.andlife.designsystem.R
import com.andlife.designsystem.component.datepicker.model.InvitationDatePickerCell
import com.andlife.designsystem.component.datepicker.model.InvitationDatePickerDate
import com.andlife.designsystem.component.datepicker.model.InvitationDatePickerYearMonth
import com.andlife.designsystem.component.datepicker.model.getDaysCountInMonth
import com.andlife.designsystem.component.datepicker.model.getFirstDayOfWeek
import com.andlife.designsystem.component.datepicker.state.InvitationDatePickerState
import com.andlife.designsystem.component.datepicker.state.rememberInvitationDatePickerState
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.InvitationSpacing
import com.andlife.designsystem.theme.InvitationTheme
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import java.time.format.TextStyle
import java.util.Locale

enum class InvitationDatePickerMode {
    DATE, // 달력에서 날짜 선택 모드
    YEAR_MONTH, // 연/월 선택 모드
}

@Composable
private fun DateCell(
    cell: InvitationDatePickerCell,
    onClick: (InvitationDatePickerDate) -> Unit,
    colors: InvitationDatePickerColors,
) {
    Box(
        modifier =
            Modifier
                .aspectRatio(1f)
                .let {
                    if (cell.isDisabled) it
                    else it.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onClick(cell.date) }
                },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize(0.7f)
                    .clip(CircleShape)
                    .background(
                        when {
                            cell.isSelected -> colors.selectedDateColor
                            cell.isToday -> colors.todayBackgroundColor
                            else -> Color.Transparent
                        },
                    ),
        )

        Text(
            text = cell.day.toString(),
            style =
                if (cell.isSelected || cell.isToday) {
                    InvitationTheme.typography.bodyMediumMedium
                } else {
                    InvitationTheme.typography.bodyMediumRegular
                },
            color =
                when {
                    cell.isDisabled -> colors.disabledTextColor
                    cell.isSelected -> colors.selectedTextColor
                    cell.isToday -> colors.todayTextColor
                    else -> colors.normalTextColor
                },
        )
    }
}


@Composable
fun InvitationDatePicker(
    state: InvitationDatePickerState,
    modifier: Modifier = Modifier,
    colors: InvitationDatePickerColors = InvitationDatePickerDefaults.colors(),
    locale: Locale = Locale.getDefault(),
) {
    val yearSuffix = stringResource(InvitationDatePickerDefaults.yearSuffixRes)
    val monthSuffix = stringResource(InvitationDatePickerDefaults.monthSuffixRes)

    Crossfade(targetState = state.mode) { mode ->
        when (mode) {
            InvitationDatePickerMode.DATE -> {
                Column(modifier) {
                    InvitationDatePickerHeader(
                        title = "${state.displayedMonth.year}$yearSuffix ${state.displayedMonth.month}$monthSuffix",
                        colors = colors,
                        onHeaderClick = state::showYearMonthSelector,
                        onPreviousClick = state::moveToPreviousMonth,
                        onNextClick = state::moveToNextMonth,
                        isClickable = true,
                    )
                    InvitationDatePickerCalendar(
                        state = state,
                        onDateClick = { date -> state.selectDate(date.date) },
                        colors = colors,
                        locale = locale,
                    )
                }
            }

            InvitationDatePickerMode.YEAR_MONTH -> {
                Column(modifier) {
                    InvitationDatePickerHeader(
                        title = "${state.displayedMonth.year}$yearSuffix",
                        colors = colors,
                        onHeaderClick = {},
                        onPreviousClick = state::moveToPreviousYear,
                        onNextClick = state::moveToNextYear,
                    )
                    InvitationDatePickerYearMonthSelector(
                        yearMonth = state.displayedMonth,
                        onMonthSelect = state::showCalendar,
                        colors = colors,
                        monthSuffix = monthSuffix,
                    )
                }
            }
        }
    }
}

@Composable
private fun InvitationDatePickerHeader(
    title: String,
    colors: InvitationDatePickerColors,
    modifier: Modifier = Modifier,
    onHeaderClick: () -> Unit = {},
    onPreviousClick: () -> Unit = {},
    onNextClick: () -> Unit = {},
    isClickable: Boolean = false,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(InvitationSpacing.large),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_chevron_left_24),
            contentDescription = stringResource(R.string.desc_previous_month_or_year),
            modifier =
                Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onPreviousClick() }
                    .padding(InvitationSpacing.small),
            tint = colors.navigationColor,
        )

        // 제목 (클릭 가능)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .let { modifier ->
                        if (isClickable) {
                            modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) { onHeaderClick() }
                        } else {
                            modifier
                        }
                    }.padding(InvitationSpacing.small),
        ) {
            Text(
                text = title,
                style = InvitationTheme.typography.headingMedium,
                color = colors.headerTextColor,
            )
            if (isClickable) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_drop_down_24),
                    contentDescription = stringResource(R.string.desc_to_year_month_mode),
                    tint = colors.navigationColor,
                )
            }
        }

        Icon(
            painter = painterResource(id = R.drawable.ic_chevron_right_24),
            contentDescription = stringResource(R.string.desc_next_month_or_year),
            modifier =
                Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onNextClick() }
                    .padding(InvitationSpacing.small),
            tint = colors.navigationColor,
        )
    }
}

@Composable
private fun InvitationDatePickerCalendar(
    state: InvitationDatePickerState,
    onDateClick: (InvitationDatePickerDate) -> Unit,
    colors: InvitationDatePickerColors,
    locale: Locale,
    modifier: Modifier = Modifier,
) {
    val daysCountInMonth = state.displayedMonth.getDaysCountInMonth()
    val firstDayOfWeek = state.displayedMonth.getFirstDayOfWeek()

    Column(modifier = modifier.padding(horizontal = InvitationSpacing.large)) {
        // 요일 헤더
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            val dayOfWeeks =
                DayOfWeek.entries.map {
                    it.getDisplayName(TextStyle.NARROW, locale)
                }
            dayOfWeeks.forEach { dayOfWeek ->
                Text(
                    text = dayOfWeek,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = InvitationTheme.typography.bodyMediumSemiBold,
                    color = colors.weekdayTextColor,
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 날짜 그리드
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth(),
        ) {
            // 빈 공간 (월의 첫 번째 날 이전)
            items(firstDayOfWeek % 7) {
                Box(modifier = Modifier.size(28.dp))
            }

            // State에서 미리 계산된 dateCells 사용
            items(daysCountInMonth,
                { index ->
                    state.dateCells[index].date.toString()
                }) { index ->
                val cell = state.dateCells[index]

                DateCell(
                    cell = cell,
                    onClick = onDateClick,
                    colors = colors,
                )
            }
        }
    }
}

@Composable
private fun InvitationDatePickerYearMonthSelector(
    yearMonth: InvitationDatePickerYearMonth,
    onMonthSelect: (InvitationDatePickerYearMonth) -> Unit,
    colors: InvitationDatePickerColors,
    monthSuffix: String,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(InvitationSpacing.medium),
        modifier = modifier.padding(),
        contentPadding = PaddingValues(horizontal = InvitationSpacing.large),
    ) {
        items(12) { index ->
            val month = index + 1
            val isCurrentMonth = month == yearMonth.month

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clip(InvitationTheme.shapes.small)
                        .background(
                            if (isCurrentMonth) {
                                colors.selectedMonthColor
                            } else {
                                Color.Transparent
                            },
                        ).clickable {
                            onMonthSelect(InvitationDatePickerYearMonth(yearMonth.year, month))
                        },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = (index + 1).toString() + monthSuffix,
                    style =
                        if (isCurrentMonth) {
                            InvitationTheme.typography.bodyMediumSemiBold
                        } else {
                            InvitationTheme.typography.bodyMediumRegular
                        },
                    color =
                        if (isCurrentMonth) {
                            colors.selectedTextColor
                        } else {
                            colors.normalTextColor
                        },
                )
            }
        }
    }
}

@PreviewTheme
@Composable
private fun InvitationDatePickerPreview() {
    InvitationTheme {
        InvitationDatePicker(
            state = rememberInvitationDatePickerState(),
        )
    }
}

@PreviewTheme
@Composable
private fun DatePickerWithSelectedInvitationDatePreview() {
    InvitationTheme {
        InvitationDatePicker(
            state =
                rememberInvitationDatePickerState(
                    initialSelectedDate = LocalDate(2025, 12, 31),
                ),
        )
    }
}

@PreviewTheme
@Composable
private fun InvitationDatePickerWithYearMonthModePreview() {
    InvitationTheme {
        InvitationDatePicker(
            state =
                rememberInvitationDatePickerState(
                    initialMode = InvitationDatePickerMode.YEAR_MONTH,
                ),
        )
    }
}
