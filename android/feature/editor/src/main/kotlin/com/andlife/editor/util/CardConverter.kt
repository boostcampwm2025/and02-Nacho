package com.andlife.editor.util

import android.graphics.Typeface
import android.text.Editable
import android.text.Layout
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.AlignmentSpan
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import androidx.core.graphics.toColorLong
import com.andlife.invitation_card.editor.utils.CenteredImageSpan
import com.andlife.model.editor.CardImage
import com.andlife.model.editor.RichTextUiContent
import com.andlife.model.editor.SpanUiStyle
import com.andlife.model.editor.StyledUiSpan
import com.andlife.model.editor.TextUiAlignment
import javax.inject.Inject
import kotlin.text.startsWith

interface CardConverter {
    fun toRichTextContent(editable: Editable): RichTextUiContent
    fun toSpannable(content: RichTextUiContent): SpannableStringBuilder
    fun findImagePlaceholderPositions(text: String): List<Int>
}

class CardConverterImpl @Inject constructor() : CardConverter {

    override fun toRichTextContent(editable: Editable): RichTextUiContent {
        val text = editable.toString()

        val styledSpans = mutableListOf<StyledUiSpan>()

        editable.getSpans(0, editable.length, StyleSpan::class.java).forEach { span ->
            val start = editable.getSpanStart(span)
            val end = editable.getSpanEnd(span)
            when (span.style) {
                Typeface.BOLD -> styledSpans.add(StyledUiSpan(start, end, SpanUiStyle.Bold))
                Typeface.ITALIC -> styledSpans.add(StyledUiSpan(start, end, SpanUiStyle.Italic))
                Typeface.BOLD_ITALIC -> {
                    styledSpans.add(StyledUiSpan(start, end, SpanUiStyle.Bold))
                    styledSpans.add(StyledUiSpan(start, end, SpanUiStyle.Italic))
                }
            }
        }

        editable.getSpans(0, editable.length, UnderlineSpan::class.java).forEach { span ->
            val start = editable.getSpanStart(span)
            val end = editable.getSpanEnd(span)
            styledSpans.add(StyledUiSpan(start, end, SpanUiStyle.Underline))
        }

        editable.getSpans(0, editable.length, StrikethroughSpan::class.java).forEach { span ->
            val start = editable.getSpanStart(span)
            val end = editable.getSpanEnd(span)
            styledSpans.add(StyledUiSpan(start, end, SpanUiStyle.Strikethrough))
        }

        editable.getSpans(0, editable.length, AbsoluteSizeSpan::class.java).forEach { span ->
            val start = editable.getSpanStart(span)
            val end = editable.getSpanEnd(span)
            val size = span.size.toFloat()
            styledSpans.add(StyledUiSpan(start, end, SpanUiStyle.FontSize(size)))
        }

        editable.getSpans(0, editable.length, ForegroundColorSpan::class.java).forEach { span ->
            val start = editable.getSpanStart(span)
            val end = editable.getSpanEnd(span)
            val color = span.foregroundColor.toColorLong()
            styledSpans.add(StyledUiSpan(start, end, SpanUiStyle.TextColor(color)))
        }

        editable.getSpans(0, editable.length, AlignmentSpan.Standard::class.java).forEach { span ->
            val start = editable.getSpanStart(span)
            val end = editable.getSpanEnd(span)
            val alignment = when (span.alignment) {
                Layout.Alignment.ALIGN_NORMAL -> TextUiAlignment.LEFT
                Layout.Alignment.ALIGN_CENTER -> TextUiAlignment.CENTER
                Layout.Alignment.ALIGN_OPPOSITE -> TextUiAlignment.RIGHT
                else -> TextUiAlignment.LEFT
            }
            styledSpans.add(StyledUiSpan(start, end, SpanUiStyle.Alignment(alignment)))
        }

        val imageSpans = editable.getSpans(0, editable.length, CenteredImageSpan::class.java)
            .sortedBy { editable.getSpanStart(it) }

        val images = imageSpans.map { span ->
            val source = span.imageSource
            if ((source.startsWith("http://") || source.startsWith("https://"))) {
                CardImage.Remote(url = source)
            } else {
                CardImage.Local(uri = source)
            }
        }

        return RichTextUiContent(
            text = text,
            spans = styledSpans,
            images = images
        )
    }

    override fun toSpannable(content: RichTextUiContent): SpannableStringBuilder {
        val spannable = SpannableStringBuilder(content.text)

        content.spans.forEach { styledSpan ->
            val start = styledSpan.start.coerceIn(0, spannable.length)
            val end = styledSpan.end.coerceIn(start, spannable.length)

            val androidSpan = when (val style = styledSpan.style) {
                is SpanUiStyle.Bold -> StyleSpan(Typeface.BOLD)
                is SpanUiStyle.Italic -> StyleSpan(Typeface.ITALIC)
                is SpanUiStyle.Underline -> UnderlineSpan()
                is SpanUiStyle.Strikethrough -> StrikethroughSpan()
                is SpanUiStyle.FontSize -> AbsoluteSizeSpan(style.size.toInt(), true)
                is SpanUiStyle.TextColor -> ForegroundColorSpan(style.color.toInt())
                is SpanUiStyle.BackgroundColor -> BackgroundColorSpan(style.color.toInt())
                is SpanUiStyle.Alignment -> {
                    val alignment = when (style.alignment) {
                        TextUiAlignment.LEFT -> Layout.Alignment.ALIGN_NORMAL
                        TextUiAlignment.CENTER -> Layout.Alignment.ALIGN_CENTER
                        TextUiAlignment.RIGHT -> Layout.Alignment.ALIGN_OPPOSITE
                    }
                    AlignmentSpan.Standard(alignment)
                }
            }
            spannable.setSpan(androidSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        return spannable
    }

    override fun findImagePlaceholderPositions(text: String): List<Int> {
        val positions = mutableListOf<Int>()
        text.forEachIndexed { index, char ->
            if (char == RichTextUiContent.IMAGE_PLACEHOLDER) {
                positions.add(index)
            }
        }
        return positions
    }
}
