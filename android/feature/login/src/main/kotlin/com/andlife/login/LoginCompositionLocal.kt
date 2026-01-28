package com.andlife.login

import androidx.compose.runtime.staticCompositionLocalOf
import com.andlife.login.social.LoginManager

val LocalLoginManager = staticCompositionLocalOf<LoginManager> {
    error("LoginManager가 설정되지 않았습니다.")
}
