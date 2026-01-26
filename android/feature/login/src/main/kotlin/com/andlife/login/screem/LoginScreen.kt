package com.andlife.login.screem

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoStroke
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.domain.util.onFailure
import com.andlife.domain.util.onSuccess
import com.andlife.login.LocalLoginManager
import com.andlife.login.R
import com.andlife.login.social.SocialType
import kotlinx.coroutines.launch

@Composable
fun LoginRoute(
    modifier: Modifier = Modifier
) {
    val loginManager = LocalLoginManager.current
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val res = LocalResources.current

    LoginScreen(
        modifier = modifier,
        snackbarHostState = snackbarHostState,
        onSocialLogin = { socialType ->
            scope.launch {
                loginManager.login(
                    socialType = socialType,
                    context = context
                )
                    .onSuccess {
                        Log.d("Login", "Login success: $it")
                    }
                    .onFailure { error, msg ->
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarHostState.showSnackbar(res.getString(R.string.fail_kakao_login))
                    }
            }
        }
    )
}

@Composable
private fun LoginScreen(
    snackbarHostState: SnackbarHostState,
    onSocialLogin: (SocialType) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        containerColor = NachoTheme.colorScheme.backgroundPrimary,
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(NachoSpacing.large)
        ) {

            Spacer(modifier = Modifier.fillMaxHeight(0.7f))
            KakaoLoginButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = NachoSpacing.large),
                onLoginClick = { onSocialLogin(SocialType.KAKAO) }
            )
            GuestLoginButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = NachoSpacing.large),
                onGuestLoginClick = {}
            )

            PolicyAndTermsText(
                modifier = Modifier.padding(top = NachoSpacing.large),
                onPrivacyPolicyClick = {
                    Log.d("Login", "onPrivacyPolicyClick")
                },
                onTermsOfServiceClick = {
                    Log.d("Login", "onTermsOfServiceClick")
                }
            )
            Spacer(modifier = Modifier.fillMaxHeight(0.05f))
        }
    }
}

@Composable
private fun KakaoLoginButton(
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit,
    shape: Shape = RoundedCornerShape(NachoSpacing.medium),
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = Color(0xFFFEE500),
        onClick = onLoginClick
    ) {
        Row(
            modifier = Modifier.padding(
                vertical = NachoSpacing.medium,
                horizontal = NachoSpacing.large
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_kakao_logo),
                contentDescription = null,
                tint = Color(0xFF000000)
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(R.string.txt_kakao_login),
                color = Color(0xFF000000).copy(alpha = 0.85f),
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun GuestLoginButton(
    onGuestLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(NachoSpacing.medium)
) {
    Surface(
        modifier = modifier,
        shape = shape,
        shadowElevation = 1.dp,
        border = BorderStroke(
            width = NachoStroke.small,
            color = NachoTheme.colorScheme.backgroundBorder
        ),
        color = NachoTheme.colorScheme.backgroundPrimary,
        onClick = onGuestLoginClick
    ) {
        Row(
            modifier = Modifier.padding(
                vertical = NachoSpacing.medium,
                horizontal = NachoSpacing.large
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_baseline_person_24),
                contentDescription = null,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(stringResource(R.string.txt_guest_login))
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
            append("로그인 시 ")

            withLink(
                LinkAnnotation.Clickable(
                    tag = "TERMS",
                    styles = TextLinkStyles(style = linkStyle),
                    linkInteractionListener = { _ -> onTermsOfServiceClick() }
                )
            ) {
                append("이용약관")
            }

            append(" 및 ")

            withLink(
                LinkAnnotation.Clickable(
                    tag = "PRIVACY",
                    styles = TextLinkStyles(style = linkStyle),
                    linkInteractionListener = { _ -> onPrivacyPolicyClick() }
                )
            ) {
                append("개인정보처리방침")
            }

            append("에\n동의하는 것으로 간주됩니다.")
        }

        Text(
            text = annotatedString,
            modifier = modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
@PreviewTheme
private fun LoginScreenPreview() {
    NachoTheme {
        LoginScreen(
            snackbarHostState = remember { SnackbarHostState() },
            onSocialLogin = {}
        )
    }
}
