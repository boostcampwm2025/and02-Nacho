package com.andlife.editor.util

import androidx.compose.runtime.Stable
import com.andlife.ui.R
import kotlinx.collections.immutable.persistentListOf

@Stable
object EffectDefaults {
    val effects = persistentListOf(
        Effect(R.string.effect_snow, R.raw.snow),
        Effect(R.string.effect_party, R.raw.bubble),
        Effect(R.string.effect_cherry_blossom, R.raw.cherryblossom)
    )
}

data class Effect(
    val nameResId: Int,
    val resId: Int
)
