package com.andlife.login.screem

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoSpacing
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

    LoginScreen(
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
                        Log.e("Login", "Login error: $error, $msg")
                    }
            }
        }
    )
}

@Composable
private fun LoginScreen(
    onSocialLogin: (SocialType) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        containerColor = NachoTheme.colorScheme.backgroundPrimary
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            SocialType.entries.forEach { socialType ->
                SocialLoginButton(
                    socialType = socialType,
                    onSocialLogin = onSocialLogin
                )
            }
        }
    }
}

@Composable
private fun SocialLoginButton(
    socialType: SocialType,
    onSocialLogin: (SocialType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Image(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = NachoSpacing.large)
            .clip(RoundedCornerShape(NachoSpacing.twoXLarge))
            .clickable { onSocialLogin(socialType) },
        painter = painterResource(id = getSocialImage(socialType)),
        contentScale = ContentScale.Crop,
        contentDescription = null
    )
}

private fun getSocialImage(socialType: SocialType): Int {
    return when (socialType) {
        SocialType.KAKAO -> R.drawable.ic_login_kakao
    }
}

@Composable
@PreviewTheme
private fun LoginScreenPreview() {
    NachoTheme {
        LoginScreen(
            onSocialLogin = {}
        )
    }
}
