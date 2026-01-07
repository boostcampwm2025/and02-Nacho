package com.andlife.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.InvitationStroke
import com.andlife.designsystem.theme.InvitationTheme
import kotlinx.coroutines.launch

@Composable
fun GenericTabRow(
    tabs: List<String>,
    content: @Composable (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier.fillMaxSize()) {
        SecondaryTabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = InvitationTheme.colorScheme.backgroundPrimary,
            contentColor = InvitationTheme.colorScheme.brandPrimary,
            divider = {
                HorizontalDivider(
                    thickness = InvitationStroke.small,
                    color = InvitationTheme.colorScheme.textTertiary,
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                val isSelected = pagerState.currentPage == index
                Tab(
                    selected = isSelected,
                    onClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(index) }
                    },
                    text = {
                        Text(
                            text = title,
                            style = InvitationTheme.typography.bodyMediumSemiBold,
                        )
                    },
                    selectedContentColor = InvitationTheme.colorScheme.brandPrimary,
                    unselectedContentColor = InvitationTheme.colorScheme.textTertiary
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.Top
        ) { pageIndex ->
            content(pageIndex)
        }
    }
}

@PreviewTheme
@Composable
private fun GenericTabScreenPreview() {
    InvitationTheme {
        GenericTabRow(
            tabs = listOf("Tab 1", "Tab 2", "Tab 3"),
            content = { index ->
                when(index) {
                    0 -> Text(text = "Tab 1 Content")
                    1 -> Text(text = "Tab 2 Content")
                    2 -> Text(text = "Tab 3 Content")
                }
            }
        )
    }
}
