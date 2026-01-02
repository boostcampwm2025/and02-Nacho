package com.andlife.ui.component.paging

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.paging.LoadState
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.InvitationTheme
import com.andlife.ui.R
import com.andlife.ui.component.loading.InvitationLoadingError
import com.andlife.ui.component.loading.InvitationLoadingIndicator

@Composable
fun PagingStateContent(
    modifier: Modifier = Modifier,
    loadState: LoadState,
    itemCount: Int,
    onRetry: () -> Unit,
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
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = stringResource(R.string.label_paging_empty_result))
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
    InvitationTheme {
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
    InvitationTheme {
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
    InvitationTheme {
        PagingStateContent(
            loadState = LoadState.NotLoading(endOfPaginationReached = true),
            itemCount = 0,
            onRetry = {},
            content = { Text("데이터 로드 완료") },
        )
    }
}
