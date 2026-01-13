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
import androidx.compose.foundation.lazy.grid.items
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
import com.andlife.designsystem.component.datepicker.model.NachoDatePickerCell
import com.andlife.designsystem.component.datepicker.model.NachoDatePickerDate
import com.andlife.designsystem.component.datepicker.model.NachoDatePickerYearMonth
import com.andlife.designsystem.component.datepicker.model.getFirstDayOfWeek
import com.andlife.designsystem.component.datepicker.state.NachoDatePickerState
import com.andlife.designsystem.component.datepicker.state.rememberInvitationDatePickerState
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.InvitationTheme
import com.andlife.designsystem.theme.NachoTheme
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
    cell: NachoDatePickerCell,
    onClick: (NachoDatePickerDate) -> Unit,
    colors: InvitationDatePickerColors,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .aspectRatio(1f)
                .let {
                    if (cell.isDisabled) {
                        it
                    } else {
                        it.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { onClick(cell.date) }
                    }
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
                    NachoTheme.typography.bodyMediumMedium
                } else {
                    NachoTheme.typography.bodyMediumRegular
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
fun NachoDatePicker(
    state: NachoDatePickerState,
    modifier: Modifier = Modifier,
    colors: InvitationDatePickerColors = NachoDatePickerDefaults.colors(),
    locale: Locale = Locale.getDefault(),
) {
    val yearSuffix = stringResource(NachoDatePickerDefaults.yearSuffixRes)
    val monthSuffix = stringResource(NachoDatePickerDefaults.monthSuffixRes)

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
                .padding(NachoSpacing.large),
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
                    .padding(NachoSpacing.small),
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
                    }.padding(NachoSpacing.small),
        ) {
            Text(
                text = title,
                style = NachoTheme.typography.headingMedium,
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
                    .padding(NachoSpacing.small),
            tint = colors.navigationColor,
        )
    }
}

@Composable
private fun InvitationDatePickerCalendar(
    state: NachoDatePickerState,
    onDateClick: (NachoDatePickerDate) -> Unit,
    colors: InvitationDatePickerColors,
    locale: Locale,
    modifier: Modifier = Modifier,
) {
    val firstDayOfWeek = state.displayedMonth.getFirstDayOfWeek()

    Column(modifier = modifier.padding(horizontal = NachoSpacing.large)) {
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
                    style = NachoTheme.typography.bodyMediumSemiBold,
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
            items(
                state.dateCells,
                { cell ->
                    cell.date.toString()
                },
            ) { cell ->
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
    yearMonth: NachoDatePickerYearMonth,
    onMonthSelect: (NachoDatePickerYearMonth) -> Unit,
    colors: InvitationDatePickerColors,
    monthSuffix: String,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(NachoSpacing.medium),
        modifier = modifier.padding(),
        contentPadding = PaddingValues(horizontal = NachoSpacing.large),
    ) {
        items(12) { index ->
            val month = index + 1
            val isCurrentMonth = month == yearMonth.month

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clip(NachoTheme.shapes.small)
                        .background(
                            if (isCurrentMonth) {
                                colors.selectedMonthColor
                            } else {
                                Color.Transparent
                            },
                        ).clickable {
                            onMonthSelect(NachoDatePickerYearMonth(yearMonth.year, month))
                        },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = (index + 1).toString() + monthSuffix,
                    style =
                        if (isCurrentMonth) {
                            NachoTheme.typography.bodyMediumSemiBold
                        } else {
                            NachoTheme.typography.bodyMediumRegular
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
        NachoDatePicker(
            state = rememberInvitationDatePickerState(),
        )
    }
}

@PreviewTheme
@Composable
private fun DatePickerWithSelectedInvitationDatePreview() {
    InvitationTheme {
        NachoDatePicker(
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
        NachoDatePicker(
            state =
                rememberInvitationDatePickerState(
                    initialMode = InvitationDatePickerMode.YEAR_MONTH,
                ),
        )
    }
}
