package com.andlife.editor.state

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Typeface
import android.net.Uri
import android.text.Editable
import android.text.Layout
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.AbsoluteSizeSpan
import android.text.style.AlignmentSpan
import android.text.style.CharacterStyle
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.drawable.toDrawable
import com.andlife.domain.util.onFailure
import com.andlife.domain.util.onSuccess
import com.andlife.editor.model.EditTextStyle
import com.andlife.editor.model.EditorDefaults
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

    var currentBackgroundImageUrl by mutableStateOf("")
        private set

    private val textWatcher = object : TextWatcher {

        override fun afterTextChanged(s: Editable?) {
            updateToolbarState()
            val editable = s
            if (editable == null) return
            val cursor = selectionStart
            val isEnterPressed = cursor > 0 && editable.isNotEmpty() && editable[cursor - 1] == '\n'
            if (isEnterPressed) {
                val alignment = currentTextStyle.alignment
                if (alignment != Layout.Alignment.ALIGN_NORMAL) {
                    s.insert(cursor, "\u200B")
                }
            }
        }

        override fun beforeTextChanged(
            s: CharSequence?, start: Int, count: Int, after: Int
        ) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val isBackspace = before > 0 && count == 0
            if (isBackspace) {
                removeZeroLengthSpans(editText.text)
            }
        }
    }

    private val selectionStart: Int
        get() = editText.selectionStart

    private val selectionEnd: Int
        get() = editText.selectionEnd

    private val hasSelection: Boolean
        get() = editText.selectionStart != editText.selectionEnd

    fun toggleBold() = applyTextStyle(
        type = StyleSpan::class.java,
        spanFactory = { StyleSpan(Typeface.BOLD) },
        predicate = { it.style == Typeface.BOLD },
        currentState = currentTextStyle.isBold,
        updateState = { currentTextStyle = currentTextStyle.copy(isBold = it) }
    )

    fun toggleItalic() = applyTextStyle(
        type = StyleSpan::class.java,
        spanFactory = { StyleSpan(Typeface.ITALIC) },
        predicate = { it.style == Typeface.ITALIC },
        currentState = currentTextStyle.isItalic,
        updateState = { currentTextStyle = currentTextStyle.copy(isItalic = it) }
    )

    fun toggleUnderline() = applyTextStyle(
        type = UnderlineSpan::class.java,
        spanFactory = { UnderlineSpan() },
        predicate = { true },
        currentState = currentTextStyle.isUnderline,
        updateState = { currentTextStyle = currentTextStyle.copy(isUnderline = it) }
    )

    fun toggleStrikethrough() {
        applyTextStyle(
            type = StrikethroughSpan::class.java,
            spanFactory = { StrikethroughSpan() },
            predicate = { true },
            currentState = currentTextStyle.isStrikethrough,
            updateState = { currentTextStyle = currentTextStyle.copy(isStrikethrough = it) }
        )
    }

    fun updateTextColor(color: Color) {
        val editable = editText.text ?: return
        val start = selectionStart
        val end = selectionEnd
        val newColorInt = color.toArgb()

        when {
            hasSelection -> {
                val spans = editable.getSpans(start, end, ForegroundColorSpan::class.java)

                for (span in spans) {
                    val spanStart = editable.getSpanStart(span)
                    val spanEnd = editable.getSpanEnd(span)
                    val existingColor = span.foregroundColor

                    editable.removeSpan(span)

                    if (spanStart < start) {
                        editable.setSpan(
                            ForegroundColorSpan(existingColor),
                            spanStart, start,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }

                    if (spanEnd > end) {
                        editable.setSpan(
                            ForegroundColorSpan(existingColor),
                            end, spanEnd,
                            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                        )
                    }
                }
                editable.setSpan(
                    ForegroundColorSpan(newColorInt),
                    start, end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            else -> {
                val charBefore = if (start > 0) editable.getOrNull(start - 1) else null
                val isAfterWordBoundary = isAfterWordBoundary(charBefore)

                if (isAfterWordBoundary) {
                    val spans = editable.getSpans(start, start, ForegroundColorSpan::class.java)
                    for (span in spans) {
                        val spanStart = editable.getSpanStart(span)
                        val spanEnd = editable.getSpanEnd(span)
                        val existingColor = span.foregroundColor

                        val canExtendAtCursor = spanEnd == start || (spanStart < start && spanEnd > start)

                        if (canExtendAtCursor) {
                            editable.removeSpan(span)

                            if (spanStart < start) {
                                editable.setSpan(
                                    ForegroundColorSpan(existingColor),
                                    spanStart, start,
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                                )
                            }
                        }
                    }
                    currentTextStyle = currentTextStyle.copy(textColor = color)
                    editable.setSpan(
                        ForegroundColorSpan(newColorInt),
                        start, start,
                        Spanned.SPAN_INCLUSIVE_INCLUSIVE
                    )
                }
            }
        }

    }

    fun updateBackgroundColor(color: Color) {
        currentTextStyle = currentTextStyle.copy(backgroundColor = color)
    }

    fun updateFontSize(size: Float) {
        val editable = editText.text ?: return
        val start = selectionStart
        val end = selectionEnd

        when {
            hasSelection -> {
                val spans = editable.getSpans(start, end, AbsoluteSizeSpan::class.java)
                for (span in spans) {
                    val spanStart = editable.getSpanStart(span)
                    val spanEnd = editable.getSpanEnd(span)
                    val existingSize = span.size

                    editable.removeSpan(span)

                    if (spanStart < start) {
                        editable.setSpan(
                            AbsoluteSizeSpan(existingSize, true),
                            spanStart, start,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }

                    if (spanEnd > end) {
                        editable.setSpan(
                            AbsoluteSizeSpan(existingSize, true),
                            end, spanEnd,
                            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                        )
                    }
                }

                editable.setSpan(
                    AbsoluteSizeSpan(size.toInt(), true),
                    start, end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            else -> {
                val charBefore = if (start > 0) editable.getOrNull(start - 1) else null
                val isAfterWordBoundary = isAfterWordBoundary(charBefore)

                if (isAfterWordBoundary) {
                    val spans = editable.getSpans(0, editable.length, AbsoluteSizeSpan::class.java)
                    for (span in spans) {
                        val spanStart = editable.getSpanStart(span)
                        val spanEnd = editable.getSpanEnd(span)
                        val existingSize = span.size

                        val canExtendAtCursor = spanEnd == start || (spanStart < start && spanEnd > start)

                        if (canExtendAtCursor) {
                            editable.removeSpan(span)

                            if (spanStart < start) {
                                editable.setSpan(
                                    AbsoluteSizeSpan(existingSize, true),
                                    spanStart, start,
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                                )
                            }
                        }
                    }
                    currentTextStyle = currentTextStyle.copy(fontSize = size)
                    editable.setSpan(
                        AbsoluteSizeSpan(size.toInt(), true),
                        start, start,
                        Spanned.SPAN_INCLUSIVE_INCLUSIVE
                    )
                }
            }
        }
    }

    private fun <T> applyTextStyle(
        type: Class<T>,
        spanFactory: () -> T,
        predicate: (T) -> Boolean,
        currentState: Boolean,
        updateState: (Boolean) -> Unit,
    ) {
        val editable = editText.text ?: return
        val start = selectionStart
        val end = selectionEnd

        when {
            hasSelection -> {
                val isFullyCovered = isSelectionFullyCovered(type, predicate)
                removeStyle(type, predicate, spanFactory)
                if (!isFullyCovered) {
                    editable.setSpan(spanFactory(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }

            else -> {
                val charBefore = if (start > 0) editable.getOrNull(start - 1) else null
                val isAfterWordBoundary = isAfterWordBoundary(charBefore)

                if (isAfterWordBoundary) {
                    val newState = !currentState
                    updateState(newState)
                    if (newState) {
                        editable.setSpan(spanFactory(), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                    } else {
                        removeStyle(type, predicate, spanFactory)
                    }
                    restartInput()
                }
            }
        }
    }

    private fun <T> isSelectionFullyCovered(
        type: Class<T>,
        predicate: (T) -> Boolean,
    ): Boolean {
        val editable = editText.text ?: return false
        val start = selectionStart
        val end = selectionEnd

        val spans = editable.getSpans(start, end, type)
            .filter(predicate)
            .sortedBy { editable.getSpanStart(it) }

        if (spans.isEmpty()) return false

        var coveredUntil = start
        for (span in spans) {
            val s = editable.getSpanStart(span)
            val e = editable.getSpanEnd(span)

            if (s > coveredUntil) return false
            if (e > coveredUntil) coveredUntil = e
            if (coveredUntil >= end) return true
        }
        return coveredUntil >= end
    }

    private fun <T> removeStyle(
        type: Class<T>,
        predicate: (T) -> Boolean,
        spanFactory: () -> T
    ) {
        val editable = editText.text ?: return
        val start = selectionStart
        val end = selectionEnd

        val spans = editable.getSpans(start, end, type)
            .filter(predicate)

        for (span in spans) {
            val spanStart = editable.getSpanStart(span)
            val spanEnd = editable.getSpanEnd(span)
            editable.removeSpan(span)

            if (spanStart < start) {
                editable.setSpan(
                    spanFactory(),
                    spanStart,
                    start,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            if (spanEnd > end) {
                editable.setSpan(
                    spanFactory(),
                    end,
                    spanEnd,
                    Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                )
            }
        }
    }

    suspend fun insertImage(uri: Uri) {
        val targetWidth = editText.width.takeIf { it > 0 }
            ?: (editText.resources.displayMetrics.widthPixels * 0.7f).toInt()
        val targetHeight = (targetWidth * 9f / 16f).toInt()
        val context = editText.context
        imageLoader.loadBitmap(context, uri, targetWidth, targetHeight)
            .onSuccess { bitmap ->
                editText.insertImageSpan(bitmap, uri.toString())
            }
            .onFailure { error ->
                Log.e("EditorState", "insertImage: $error")
            }
    }

    fun alignLeft() = updateAlignment(Layout.Alignment.ALIGN_NORMAL)
    fun alignCenter() = updateAlignment(Layout.Alignment.ALIGN_CENTER)
    fun alignRight() = updateAlignment(Layout.Alignment.ALIGN_OPPOSITE)

    private fun updateAlignment(alignment: Layout.Alignment) {
        val editable = editText.text ?: return
        val start = selectionStart
        val end = selectionEnd

        val lineStart = findParagraphStart(editable, start)
        val lineEnd = findParagraphEnd(editable, if (hasSelection) end else start)

        if (lineStart == lineEnd) {
            editable.insert(lineStart, "$EMPTY_TEXT")
            editText.setSelection(lineStart + 1)
            updateAlignment(alignment)
            return
        }

        val existingSpans = editable.getSpans(lineStart, lineEnd, AlignmentSpan.Standard::class.java)
        for (span in existingSpans) {
            val spanStart = editable.getSpanStart(span)
            val spanEnd = editable.getSpanEnd(span)
            val existingAlignment = span.alignment

            editable.removeSpan(span)

            if (spanStart < lineStart) {
                editable.setSpan(
                    AlignmentSpan.Standard(existingAlignment),
                    spanStart, lineStart,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            if (spanEnd > lineEnd) {
                editable.setSpan(
                    AlignmentSpan.Standard(existingAlignment),
                    lineEnd, spanEnd,
                    Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                )
            }
        }
        editText.setSelection(start, end)
        if (alignment != Layout.Alignment.ALIGN_NORMAL) {
            editable.setSpan(
                AlignmentSpan.Standard(alignment),
                lineStart, lineEnd,
                Spanned.SPAN_INCLUSIVE_INCLUSIVE
            )
        }

        currentTextStyle = currentTextStyle.copy(alignment = alignment)

        if (!hasSelection && start == lineStart) {
            val newPos = (start + 1).coerceAtMost(editable.length)
            if (editable[start] == EMPTY_TEXT) {
                editText.setSelection(newPos)
            }
        }
    }

    private fun removeZeroLengthSpans(editable: Editable) {
        val spans = editable.getSpans(0, editable.length, CharacterStyle::class.java)
        for (span in spans) {
            if ((editable.getSpanFlags(span) and Spanned.SPAN_COMPOSING) != 0) continue

            if (editable.getSpanStart(span) == editable.getSpanEnd(span)) {
                editable.removeSpan(span)
            }
        }
    }

    private fun findParagraphStart(editable: Editable, position: Int): Int {
        var pos = position.coerceIn(0, editable.length)
        while (pos > 0 && editable[pos - 1] != '\n') {
            pos--
        }
        return pos
    }

    private fun findParagraphEnd(editable: Editable, position: Int): Int {
        var pos = position.coerceIn(0, editable.length)
        while (pos < editable.length && editable[pos] != '\n') {
            pos++
        }
        if (pos < editable.length) {
            pos++
        }
        return pos
    }

    private fun isAfterWordBoundary(charBefore: Char?): Boolean {
        return charBefore == null || charBefore == SPACE || charBefore == NEXT_LINE || charBefore == EMPTY_TEXT
    }

    private fun updateToolbarState() {
        val editable = editText.text ?: return
        val start = selectionStart
        val end = selectionEnd

        if (start < 0 || end < 0) return
        val checkStart = if (start == end && start > 0) start else start
        val checkEnd = if (start == end && start > 0) start else end

        fun <T> getActiveSpans(type: Class<T>, predicate: (T) -> Boolean): List<T> {
            val spans = editable.getSpans(checkStart, checkEnd, type)
                .filter(predicate)
            if (spans.isEmpty()) return emptyList()

            return spans.filter { span ->
                val spanStart = editable.getSpanStart(span)
                val spanEnd = editable.getSpanEnd(span)
                val flags = editable.getSpanFlags(span)

                val isInside = start > spanStart && start < spanEnd
                val isAtStartWithInclusive = start == spanStart &&
                    (flags and Spanned.SPAN_INCLUSIVE_INCLUSIVE) != 0
                val isAtEndWithInclusive = start == spanEnd &&
                    (flags and Spanned.SPAN_INCLUSIVE_INCLUSIVE) != 0

                (isInside || isAtStartWithInclusive || isAtEndWithInclusive)
            }
        }

        fun <T> hasTextStyle(type: Class<T>, predicate: (T) -> Boolean): Boolean {
            return getActiveSpans(type, predicate).isNotEmpty()
        }

        val currentColor = getActiveSpans(ForegroundColorSpan::class.java) { true }
            .firstOrNull()
            ?.let { Color(it.foregroundColor) }
            ?: EditorDefaults.Black

        val currentFontSize = getActiveSpans(AbsoluteSizeSpan::class.java) { true }
            .firstOrNull()
            ?.size
            ?.toFloat()
            ?: EditorDefaults.DEFAULT_TEXT_SIZE

        val lineStart = findParagraphStart(editable, start)
        val lineEnd = findParagraphEnd(editable, start)

        val alignmentSpans = editable.getSpans(lineStart, lineEnd, AlignmentSpan.Standard::class.java)
        val currentAlignment = alignmentSpans.lastOrNull()?.alignment
            ?: Layout.Alignment.ALIGN_NORMAL

        currentTextStyle = currentTextStyle.copy(
            isBold = hasTextStyle(StyleSpan::class.java) { it.style == Typeface.BOLD },
            isItalic = hasTextStyle(StyleSpan::class.java) { it.style == Typeface.ITALIC },
            isUnderline = hasTextStyle(UnderlineSpan::class.java) { true },
            isStrikethrough = hasTextStyle(StrikethroughSpan::class.java) { true },
            textColor = currentColor,
            fontSize = currentFontSize,
            alignment = currentAlignment
        )
    }

    fun restartInput() {
        val imm = editText.context.getSystemService(Context.INPUT_METHOD_SERVICE)
            as? InputMethodManager
        imm?.restartInput(editText)
    }

    fun clearFocusAndHideKeyboard() {
        editText.clearFocus()
        val imm = editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    fun setEditable(editable: Editable) {
        editText.text = editable
    }

    fun setBackground(color: Color) {
        currentTextStyle = currentTextStyle.copy(backgroundColor = color)
    }

    fun attach(view: EditText) {
        editText = view

        val cursorListener = View.OnClickListener {
            updateToolbarState()
        }
        view.addTextChangedListener(textWatcher)
        view.setOnClickListener(cursorListener)
        view.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                updateToolbarState()
            }
        }
        view.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                val text = editText.text
                val cursor = editText.selectionStart
                if (cursor > 0 && text.length >= cursor && text[cursor - 1] == EMPTY_TEXT) {
                    val hasNewLineBefore = cursor - 2 >= 0 && text[cursor - 2] == NEXT_LINE
                    if (hasNewLineBefore) {
                        text.delete(cursor - 2, cursor)
                    } else {
                        text.delete(cursor - 1, cursor)
                    }
                    return@setOnKeyListener true
                }
            }
            false
        }
    }

    companion object {
        private const val EMPTY_TEXT = '\u200B'
        private const val NEXT_LINE = '\n'
        private const val SPACE = ' '
    }
}

private fun EditText.insertImageSpan(bitmap: Bitmap, imageSource: String) {
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

    val imageSpan = CenteredImageSpan(drawable, this.width, imageSource)

    editable.setSpan(imageSpan, imageStart, imageEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

    this.setSelection(cursorPos + insertText.length)
}
