package com.andlife.ui.component.paging

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.paging.LoadState
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.ui.R
import com.andlife.ui.component.loading.InvitationLoadingError
import com.andlife.ui.component.loading.InvitationLoadingIndicator

@Composable
fun PagingStateContent(
    loadState: LoadState,
    itemCount: Int,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    emptyComment: String = stringResource(R.string.label_paging_empty_result),
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (loadState) {
            is LoadState.Loading -> {
                InvitationLoadingIndicator()
            }

            is LoadState.Error -> {
                InvitationLoadingError(onRetry = onRetry)
            }

            is LoadState.NotLoading -> {
                if (itemCount == 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = emptyComment,
                            style = NachoTheme.typography.bodyLargeMedium,
                            color = NachoTheme.colorScheme.textSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = NachoTheme.typography.bodyLargeMedium.lineHeight * 1.4f
                        )
                    }
                } else {
                    content()
                }
            }
        }
    }
}

@PreviewTheme
@Composable
private fun PagingStateContentPreview_Loading() {
    NachoTheme {
        PagingStateContent(
            loadState = LoadState.Loading,
            itemCount = 0,
            onRetry = {},
            content = { Text("데이터 로드 완료") },
        )
    }
}

@PreviewTheme
@Composable
private fun PagingStateContentPreview_Error() {
    NachoTheme {
        PagingStateContent(
            loadState = LoadState.Error(Throwable("네트워크 오류 발생")),
            itemCount = 0,
            onRetry = {},
            content = { Text("데이터 로드 완료") },
        )
    }
}

@PreviewTheme
@Composable
private fun PagingStateContentPreview_Empty() {
    NachoTheme {
        PagingStateContent(
            loadState = LoadState.NotLoading(endOfPaginationReached = true),
            itemCount = 0,
            onRetry = {},
            content = { Text("데이터 로드 완료") },
        )
    }
}
