package com.andlife.editor.util

import android.graphics.Color
import android.text.Editable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreateCardSession @Inject constructor() {

    var editable: Editable? = null
        private set

    var backgroundColor: Int = Color.WHITE
        private set

    fun save(editable: Editable, backgroundColor: Int) {
        this.editable = editable
        this.backgroundColor = backgroundColor
    }

    fun clear() {
        editable = null
        backgroundColor = Color.WHITE
    }
}
