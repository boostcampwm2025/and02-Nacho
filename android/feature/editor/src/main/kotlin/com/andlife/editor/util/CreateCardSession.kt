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

    var backgroundEffect: String? = null
        private set

    fun save(editable: Editable?, backgroundColor: Color, backgroundImageUrl: String?) {
        this.editable = editable
        this.backgroundColor = backgroundColor
        this.backgroundEffect = backgroundImageUrl
    }

    fun clear() {
        editable = null
        backgroundColor = Color.White
        backgroundEffect = null
    }
}
