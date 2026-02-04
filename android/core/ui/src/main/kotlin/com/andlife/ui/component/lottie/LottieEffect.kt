package com.andlife.ui.component.lottie

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalResources
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.andlife.ui.R
import kotlinx.coroutines.delay

@Composable
fun LottieEffect(
    modifier: Modifier = Modifier,
    selectEffect: String? = null,
    maxDurationMillis: Long = 4000L
) {
    val res = LocalResources.current

    if (selectEffect == null) return

    val rawResId = when (selectEffect) {
        res.getString(R.string.effect_snow) -> R.raw.snow
        res.getString(R.string.effect_party) -> R.raw.bubble
        res.getString(R.string.effect_cherry_blossom) -> R.raw.cherryblossom
        else -> return
    }
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(rawResId)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1
    )

    var isTimedOut by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(maxDurationMillis)
        isTimedOut = true
    }

    val isFinished = progress >= 1f || isTimedOut
    val alpha by animateFloatAsState(
        targetValue = if (isFinished) 0f else 1f,
        animationSpec = tween(durationMillis = 500),
        label = "lottie_fade_out"
    )

    if (alpha > 0f) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = modifier.alpha(alpha),
            contentScale = ContentScale.Crop
        )
    }
}
