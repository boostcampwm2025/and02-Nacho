package com.andlife.editor.util

import android.text.Editable
import androidx.compose.ui.graphics.Color
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreateCardSession @Inject constructor() {

    var editable: Editable? = null
        private set

    var backgroundColor: Color = Color.White
        private set

    fun save(editable: Editable, backgroundColor: Color) {
        this.editable = editable
        this.backgroundColor = backgroundColor
    }

    fun clear() {
        editable = null
        backgroundColor = Color.White
    }
}
