package com.andlife.editor.model

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import kotlinx.collections.immutable.persistentListOf

@Stable
internal object EditorDefaults {
    val Black = Color.Black
    val White = Color.White
    val Slate = Color(0xFF2E3440)        // 다크 그레이 (배경용 매우 좋음)
    val Indigo = Color(0xFF4F46E5)       // 포인트 컬러
    val Teal = Color(0xFF0D9488)         // 안정적인 강조
    val Amber = Color(0xFFF59E0B)        // 하이라이트
    val Rose = Color(0xFFE11D48)         // 감정 강조 (빨강 대체)
    val Cream = Color(0xFFFFFBEB)
    val Mint = Color(0xFFECFDF5)
    val Sky = Color(0xFFEFF6FF)
    val Lavender = Color(0xFFF5F3FF)
    val Blush = Color(0xFFFFF1F2)

    val textColorPalette = persistentListOf(
        Black,
        Slate,
        Indigo,
        Teal,
        Rose,
        Amber
    )

    val backgroundColorPalette = persistentListOf(
        White,
        Cream,
        Sky,
        Mint,
        Lavender,
        Slate,
        Blush,
    )

    const val MIN_MEDIUM_TEXT_SIZE = 12f
    const val DEFAULT_TEXT_SIZE = 16f
    const val MIN_TEXT_SIZE = 24f
    const val MAX_TEXT_SIZE = 36f

    val fontFamilies = listOf(MIN_MEDIUM_TEXT_SIZE, DEFAULT_TEXT_SIZE, MIN_TEXT_SIZE, MAX_TEXT_SIZE)
}

