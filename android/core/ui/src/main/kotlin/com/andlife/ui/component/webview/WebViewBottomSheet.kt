package com.andlife.ui.component.webview

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.viewinterop.AndroidView
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.ui.R
import com.andlife.ui.component.loading.InvitationLoadingIndicator
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewBottomSheet(
    url: String,
    title: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }

    val dismiss: () -> Unit = {
        scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = NachoTheme.colorScheme.backgroundPrimary,
        dragHandle = {},
        shape = RectangleShape,
        sheetMaxWidth = Dp.Unspecified,
        modifier = modifier.fillMaxSize(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = NachoSpacing.large)
                .padding(vertical = NachoSpacing.xLarge),
        ) {
            Text(
                text = title,
                style = NachoTheme.typography.headingSmallSemiBold,
                color = NachoTheme.colorScheme.textPrimary,
                modifier = Modifier.align(Alignment.Center),
            )
            Icon(
                painter = painterResource(R.drawable.ic_close_24),
                contentDescription = stringResource(R.string.desc_close),
                tint = NachoTheme.colorScheme.textPrimary,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clickable { dismiss() },
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            if (isError) {
                Text(
                    text = stringResource(R.string.txt_error_network),
                    style = NachoTheme.typography.bodyLargeRegular,
                    color = NachoTheme.colorScheme.textSecondary,
                )
            } else {
                AndroidView(
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(if (isLoading) 0f else 1f),
                    factory = { context ->
                        @SuppressLint("ClickableViewAccessibility")
                        WebView(context).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            setOnTouchListener { v, event ->
                                when (event.action) {
                                    MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE ->
                                        v.parent?.requestDisallowInterceptTouchEvent(true)

                                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->
                                        v.parent?.requestDisallowInterceptTouchEvent(false)
                                }
                                false
                            }
                            webViewClient = object : WebViewClient() {
                                override fun onReceivedError(
                                    view: WebView?,
                                    request: WebResourceRequest?,
                                    error: WebResourceError?
                                ) {
                                    if (request?.isForMainFrame == true) {
                                        isLoading = false
                                        isError = true
                                    }
                                }
                            }
                            webChromeClient = object : WebChromeClient() {
                                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                    if (newProgress >= 100) {
                                        isLoading = false
                                    }
                                }
                            }
                            settings.apply {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                            }
                        }.also { webview ->
                            webview.loadUrl(url)
                        }
                    },
                    onRelease = { webView ->
                        webView.destroy()
                    },
                )
                if (isLoading) {
                    InvitationLoadingIndicator()
                }
            }
        }
    }
}

@PreviewTheme
@Composable
private fun WebViewBottomSheetPreview() {
    NachoTheme {
        WebViewBottomSheet(
            url = "",
            title = "서비스 이용약관",
            onDismiss = {},
        )
    }
}
