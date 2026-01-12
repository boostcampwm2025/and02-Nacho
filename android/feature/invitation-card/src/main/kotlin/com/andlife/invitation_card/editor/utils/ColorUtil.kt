package com.andlife.invitation_card.editor.utils

import androidx.compose.ui.graphics.Color

fun Color.luminance(): Float {
    val red = this.red
    val green = this.green
    val blue = this.blue

    return 0.299f * red + 0.587f * green + 0.114f * blue
}

fun Color.contrastColor(): Color = if (this.luminance() > 0.5f) Color.Black else Color.White
