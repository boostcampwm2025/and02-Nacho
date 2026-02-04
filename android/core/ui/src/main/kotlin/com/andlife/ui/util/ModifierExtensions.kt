package com.andlife.ui.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

@Composable
fun Modifier.imeWithoutNavBars(): Modifier {
    val ime = WindowInsets.ime
    val nav = WindowInsets.navigationBars

    return this.windowInsetsPadding(
        object : WindowInsets {
            override fun getBottom(density: Density): Int {
                val imeBottom = ime.getBottom(density)
                val navBottom = nav.getBottom(density)
                return maxOf(imeBottom - navBottom, 0)
            }

            override fun getTop(density: Density) = 0
            override fun getLeft(density: Density, layoutDirection: LayoutDirection) = 0
            override fun getRight(density: Density, layoutDirection: LayoutDirection) = 0
        }
    )
}

fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
    ) {
        onClick()
    }
}
