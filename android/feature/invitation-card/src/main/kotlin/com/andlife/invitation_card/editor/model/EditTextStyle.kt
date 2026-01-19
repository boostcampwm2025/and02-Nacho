package com.andlife.invitation_card.editor.model

import android.text.Layout
import androidx.compose.ui.graphics.Color

data class EditTextStyle(
    val isBold: Boolean = false,
    val isItalic: Boolean = false,
    val isUnderline: Boolean = false,
    val isStrikethrough: Boolean = false,
    val alignment: Layout.Alignment = Layout.Alignment.ALIGN_NORMAL,
    val color: Color = EditorDefaults.Black,
    val backgroundColor: Color = EditorDefaults.White,
    val fontSize: Float = 16f,
)
