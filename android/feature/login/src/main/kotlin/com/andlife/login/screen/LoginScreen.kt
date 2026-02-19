package com.andlife.login.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andlife.designsystem.component.NachoButton
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.KakaoButtonColor
import com.andlife.designsystem.theme.KakaoTextColor
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.domain.error.LoginError
import com.andlife.domain.util.onFailure
import com.andlife.domain.util.onSuccess
import com.andlife.login.LocalLoginManager
import com.andlife.login.R
import com.andlife.login.model.LoginSideEffect
import com.andlife.login.model.LoginUiEvent
import com.andlife.login.model.LoginUiState
import com.andlife.login.social.SocialType
import com.andlife.login.viewmodel.LoginViewModel
import com.andlife.ui.component.loading.InvitationLoadingIndicator
import com.andlife.ui.util.collectWithLifecycle
import com.andlife.ui.component.webview.PolicyUrl
import com.andlife.ui.component.webview.WebViewBottomSheet
import kotlinx.coroutines.launch
import com.andlife.designsystem.R as designR

@Composable
fun LoginRoute(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    var webViewState by remember { mutableStateOf<Pair<String, String>?>(null) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val loginManager = LocalLoginManager.current
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val res = LocalResources.current

    viewModel.effectFlow.collectWithLifecycle { effect ->
        when (effect) {
            LoginSideEffect.FailGuestLogin -> {
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar(res.getString(R.string.fail_guest_login))
            }

            LoginSideEffect.FailSocialLogin -> {
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar(res.getString(R.string.fail_kakao_login))
            }

            LoginSideEffect.FailTestLogin -> {
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar(res.getString(R.string.fail_test_login))
            }
        }
    }

    LoginScreen(
        uiState = uiState,
        modifier = modifier,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent,
        onWebViewClick = { url, title -> webViewState = url to title },
        onSocialLogin = { socialType ->
            scope.launch {
                loginManager.login(
                    socialType = socialType,
                    context = context
                )
                    .onSuccess {
                        viewModel.onEvent(LoginUiEvent.SocialLoginSuccess(it))
                    }
                    .onFailure { error, msg ->
                        if (error !is LoginError.Cancel) {
                            snackbarHostState.currentSnackbarData?.dismiss()
                            snackbarHostState.showSnackbar(res.getString(R.string.fail_kakao_login))
                        }
                    }
            }
        }
    )

    webViewState?.let { (url, title) ->
        WebViewBottomSheet(
            url = url,
            title = title,
            onDismiss = { webViewState = null },
        )
    }
}

@Composable
private fun LoginScreen(
    uiState: LoginUiState,
    snackbarHostState: SnackbarHostState,
    onSocialLogin: (SocialType) -> Unit,
    onEvent: (LoginUiEvent) -> Unit,
    onWebViewClick: (String, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        containerColor = NachoTheme.colorScheme.backgroundPrimary,
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = NachoSpacing.large),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))

            logoSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = NachoSpacing.large)
            )

            Spacer(modifier = Modifier.weight(0.7f))

            Column(
                verticalArrangement = Arrangement.spacedBy(NachoSpacing.large),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                KakaoLoginButton(
                    modifier = Modifier.fillMaxWidth(),
                    onLoginClick = { onSocialLogin(SocialType.KAKAO) }
                )

                TestLoginButton(
                    modifier = Modifier.fillMaxWidth(),
                    onTestLoginClick = { onEvent(LoginUiEvent.TestLogin) }
                )

                GuestLoginButton(
                    modifier = Modifier.fillMaxWidth(),
                    onGuestLoginClick = { onEvent(LoginUiEvent.GuestLogin) }
                )
            }

            Spacer(modifier = Modifier.weight(0.5f))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(NachoSpacing.medium)
            ) {
                val serviceTitle = stringResource(R.string.termsofuse)
                val privacyTitle = stringResource(R.string.privacypolicy)

                PolicyAndTermsText(
                    onTermsOfServiceClick = {
                        onWebViewClick(PolicyUrl.SERVICE, serviceTitle)
                    },
                    onPrivacyPolicyClick = {
                        onWebViewClick(PolicyUrl.PRIVACY, privacyTitle)
                    },
                )

                CopyrightText(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = NachoSpacing.large),
                )
            }
        }
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                InvitationLoadingIndicator()
            }
        }
    }
}

@Composable
private fun logoSection(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(NachoSpacing.twoXLarge)
    ) {
        Text(
            text = stringResource(R.string.txt_login_title),
            style = NachoTheme.typography.headingLarge,
            color = NachoTheme.colorScheme.textSecondary,
        )
        Text(
            text = stringResource(R.string.txt_login_content),
            style = NachoTheme.typography.bodyLargeSemiBold,
            color = NachoTheme.colorScheme.textTertiary,
            textAlign = TextAlign.Center,
        )

        Image(
            painter = painterResource(designR.drawable.ic_home_logo),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun KakaoLoginButton(
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit,
    shape: Shape = RoundedCornerShape(NachoSpacing.medium),
    isLoading: Boolean = false
) {

    NachoButton(
        modifier = modifier,
        shape = shape,
        enabled = !isLoading,
        onClick = onLoginClick,
        containerColor = KakaoButtonColor,
        contentColor = NachoTheme.colorScheme.textSecondary,
        contentPadding = PaddingValues(NachoSpacing.large)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_kakao_logo),
                contentDescription = null,
                tint = KakaoTextColor

            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(R.string.txt_kakao_login),
                style = NachoTheme.typography.bodyLargeMedium,
                color = NachoTheme.colorScheme.textSecondary,
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun GuestLoginButton(
    onGuestLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(NachoSpacing.medium),
    isLoading: Boolean = false
) {

    NachoButton(
        modifier = modifier,
        shape = shape,
        enabled = !isLoading,
        onClick = onGuestLoginClick,
        containerColor = NachoTheme.colorScheme.backgroundPrimary,
        contentColor = NachoTheme.colorScheme.textSecondary,
        contentPadding = PaddingValues(NachoSpacing.large)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_baseline_person_24),
                contentDescription = null,
                tint = KakaoTextColor

            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(R.string.txt_guest_login),
                style = NachoTheme.typography.bodyLargeMedium,
                color = NachoTheme.colorScheme.textSecondary,
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun TestLoginButton(
    onTestLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(NachoSpacing.medium),
    isLoading: Boolean = false
) {

    NachoButton(
        modifier = modifier,
        shape = shape,
        enabled = !isLoading,
        onClick = onTestLoginClick,
        containerColor = NachoTheme.colorScheme.backgroundPrimary,
        contentColor = NachoTheme.colorScheme.textSecondary,
        contentPadding = PaddingValues(NachoSpacing.large)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_baseline_person_24),
                contentDescription = null,
                tint = KakaoTextColor

            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(R.string.txt_test_user_login),
                style = NachoTheme.typography.bodyLargeMedium,
                color = NachoTheme.colorScheme.textSecondary,
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun PolicyAndTermsText(
    modifier: Modifier = Modifier,
    onPrivacyPolicyClick: () -> Unit,
    onTermsOfServiceClick: () -> Unit
) {
    val res = LocalResources.current
    CompositionLocalProvider(
        LocalTextStyle provides NachoTheme.typography.bodySmallRegular.copy(
            color = NachoTheme.colorScheme.textTertiary
        )
    ) {
        val linkStyle = SpanStyle(
            color = NachoTheme.colorScheme.brandPrimary,
            fontWeight = FontWeight.Bold,
        )

        val annotatedString = buildAnnotatedString {
            append(res.getString(R.string.whenloggingin))

            withLink(
                LinkAnnotation.Clickable(
                    tag = "TERMS",
                    styles = TextLinkStyles(style = linkStyle),
                    linkInteractionListener = { _ -> onTermsOfServiceClick() }
                )
            ) {
                append(res.getString(R.string.termsofuse))
            }

            append(res.getString(R.string.and))

            withLink(
                LinkAnnotation.Clickable(
                    tag = "PRIVACY",
                    styles = TextLinkStyles(style = linkStyle),
                    linkInteractionListener = { _ -> onPrivacyPolicyClick() }
                )
            ) {
                append(res.getString(R.string.privacypolicy))
            }

            append(res.getString(R.string.agree))
        }

        Text(
            text = annotatedString,
            modifier = modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun CopyrightText(
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(R.string.txt_login_copyright),
        modifier = modifier.fillMaxWidth(),
        style = NachoTheme.typography.bodySmallRegular,
        color = NachoTheme.colorScheme.textTertiary,
        textAlign = TextAlign.Center,
    )
}

@Composable
@PreviewTheme
private fun LoginScreenPreview() {
    NachoTheme {
        LoginScreen(
            uiState = LoginUiState(),
            snackbarHostState = remember { SnackbarHostState() },
            onSocialLogin = {},
            onEvent = {},
            onWebViewClick = { _, _ -> },
        )
    }
}
