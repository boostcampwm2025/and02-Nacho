package com.andlife.undo.model

import android.text.SpannableStringBuilder

data class EditSnapshot(
    val content: SpannableStringBuilder,
    val cursorPosition: Int
)
