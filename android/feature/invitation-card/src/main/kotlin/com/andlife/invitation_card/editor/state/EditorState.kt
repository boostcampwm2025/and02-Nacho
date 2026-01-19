package com.andlife.invitation_card.editor.state

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.text.Spanned
import android.util.Log
import android.widget.EditText
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.graphics.drawable.toDrawable
import com.andlife.domain.util.onFailure
import com.andlife.domain.util.onSuccess
import com.andlife.invitation_card.editor.model.EditTextStyle
import com.andlife.invitation_card.editor.utils.CenteredImageSpan
import com.andlife.invitation_card.editor.utils.ImageLoader
import javax.inject.Inject

@Stable
class EditorState @Inject constructor(
    private val imageLoader: ImageLoader
) {
    lateinit var editText: EditText

    var currentTextStyle by mutableStateOf(EditTextStyle())
        private set

    private val selectionStart: Int
        get() = editText.selectionStart

    private val selectionEnd: Int
        get() = editText.selectionEnd

    private val hasSelection: Boolean
        get() = editText.selectionStart != editText.selectionEnd

    suspend fun insertImage(uri: Uri) {
        val targetWidth = editText.width.takeIf { it > 0 }
            ?: (editText.resources.displayMetrics.widthPixels * 0.7f).toInt()
        val targetHeight = (targetWidth * 9f / 16f).toInt()
        val context = editText.context
        imageLoader.loadBitmap(context, uri, targetWidth, targetHeight)
            .onSuccess {bitmap ->
                editText.insertImageSpan(bitmap)
            }
            .onFailure { error ->
                Log.e("EditorState", "insertImage: $error")
            }
    }

    private fun restartInput() {
        val imm = editText.context.getSystemService(Context.INPUT_METHOD_SERVICE)
            as? android.view.inputmethod.InputMethodManager
        imm?.restartInput(editText)
    }

    fun attach(view: EditText) {
        editText = view
    }
}

private fun EditText.insertImageSpan(bitmap: Bitmap) {
    val editable = this.text ?: return
    val cursorPos = this.selectionStart
    val context = this.context

    val drawable = bitmap.toDrawable(context.resources)
    drawable.setBounds(0, 0, bitmap.width, bitmap.height)

    val needNewLineBefore = cursorPos > 0 && editable.getOrNull(cursorPos - 1) != '\n'
    val insertText = buildString {
        if (needNewLineBefore) append('\n')
        append("\uFFFC")
        append('\n')
    }

    editable.insert(cursorPos, insertText)

    val imageStart = cursorPos + if (needNewLineBefore) 1 else 0
    val imageEnd = imageStart + 1

    val imageSpan = CenteredImageSpan(drawable, this.width)

    editable.setSpan(imageSpan, imageStart, imageEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

    this.setSelection(cursorPos + insertText.length)
}
