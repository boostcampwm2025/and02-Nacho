package com.andlife.invitation_card.editor.model

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import kotlinx.collections.immutable.toImmutableList

@Stable
internal object EditorDefaults {
    val Black = Color.Black
    val White = Color.White
    val Red = Color.Red
    val Blue = Color.Blue
    val Green = Color.Green
    val Orange = Color(0xFFFB8C00)
    val Purple = Color(0xFF8E24AA)
    val Gray = Color.DarkGray

    val palette = listOf(Black, White, Red, Blue, Green, Orange, Purple, Gray).toImmutableList()

    const val MIN_MEDIUM_TEXT_SIZE = 12f
    const val DEFAULT_TEXT_SIZE = 16f
    const val MIN_TEXT_SIZE = 24f
    const val MAX_TEXT_SIZE = 36f

    val fontFamilies = listOf(MIN_MEDIUM_TEXT_SIZE, DEFAULT_TEXT_SIZE, MIN_TEXT_SIZE, MAX_TEXT_SIZE)
}

