//package com.andlife.invitation_card.editor.utils
//
//import android.content.Context
//import android.graphics.Typeface
//import android.text.Editable
//import android.text.Layout
//import android.text.SpannableStringBuilder
//import android.text.Spanned
//import android.text.style.AbsoluteSizeSpan
//import android.text.style.AlignmentSpan
//import android.text.style.ForegroundColorSpan
//import android.text.style.StrikethroughSpan
//import android.text.style.StyleSpan
//import android.text.style.UnderlineSpan
//import com.andlife.model.editor.ImageInfo
//import com.andlife.model.editor.RichTextContent
//import com.andlife.model.editor.SpanStyle
//import com.andlife.model.editor.StyledSpan
//import com.andlife.model.editor.TextAlignment
//
///**
// * Editable → RichTextContent 변환 (저장 시)
// */
//fun Editable.toRichTextContent(): RichTextContent {
//    val spans = mutableListOf<StyledSpan>()
//    val images = mutableListOf<ImageInfo>()
//
//    getSpans(0, length, Any::class.java).forEach { span ->
//        val start = getSpanStart(span)
//        val end = getSpanEnd(span)
//
//        when (span) {
//            is StyleSpan -> {
//                when (span.style) {
//                    Typeface.BOLD -> spans.add(StyledSpan(start, end, SpanStyle.Bold))
//                    Typeface.ITALIC -> spans.add(StyledSpan(start, end, SpanStyle.Italic))
//                }
//            }
//
//            is UnderlineSpan -> {
//                spans.add(StyledSpan(start, end, SpanStyle.Underline))
//            }
//
//            is StrikethroughSpan -> {
//                spans.add(StyledSpan(start, end, SpanStyle.Strikethrough))
//            }
//
//            is AbsoluteSizeSpan -> {
//                spans.add(StyledSpan(start, end, SpanStyle.FontSize(span.size.toFloat())))
//            }
//
//            is ForegroundColorSpan -> {
//                spans.add(StyledSpan(start, end, SpanStyle.TextColor(span.foregroundColor.toLong())))
//            }
//
//            is AlignmentSpan.Standard -> {
//                val alignment = when (span.alignment) {
//                    Layout.Alignment.ALIGN_CENTER -> TextAlignment.CENTER
//                    Layout.Alignment.ALIGN_OPPOSITE -> TextAlignment.RIGHT
//                    else -> TextAlignment.LEFT
//                }
//                spans.add(StyledSpan(start, end, SpanStyle.Alignment(alignment)))
//            }
//
//            is CenteredImageSpan -> {
//                span.imageSource?.let { source ->
//                    val isUrl = source.startsWith("http://") || source.startsWith("https://")
//                    if (isUrl) {
//                        images.add(ImageInfo(url = source))
//                    } else {
//                        images.add(ImageInfo(localUri = source))
//                    }
//                }
//            }
//        }
//    }
//
//    return RichTextContent(
//        text = toString(),
//        spans = spans,
//        images = images,
//    )
//}
//
///**
// * RichTextContent → SpannableStringBuilder 변환 (TextView/EditText에 적용)
// *
// * @param context Context (이미지 로드에 필요)
// * @param containerWidth 이미지 컨테이너 너비
// * @param onImageLoad 이미지 로드 콜백 (비동기 로드 후 span 적용)
// */
//fun RichTextContent.toSpannable(
//    context: Context,
//    containerWidth: Int,
//    onImageLoad: ((index: Int, span: CenteredImageSpan) -> Unit)? = null,
//): SpannableStringBuilder {
//    val spannable = SpannableStringBuilder(text)
//
//    // 텍스트 스타일 적용
//    spans.forEach { styledSpan ->
//        val androidSpan: Any? = when (val style = styledSpan.style) {
//            SpanStyle.Bold -> StyleSpan(Typeface.BOLD)
//            SpanStyle.Italic -> StyleSpan(Typeface.ITALIC)
//            SpanStyle.Underline -> UnderlineSpan()
//            SpanStyle.Strikethrough -> StrikethroughSpan()
//            is SpanStyle.FontSize -> AbsoluteSizeSpan(style.size.toInt(), true)
//            is SpanStyle.TextColor -> ForegroundColorSpan(style.color.toInt())
//            is SpanStyle.BackgroundColor -> null // TODO: BackgroundColorSpan 적용
//            is SpanStyle.Alignment -> {
//                val alignment = when (style.alignment) {
//                    TextAlignment.LEFT -> Layout.Alignment.ALIGN_NORMAL
//                    TextAlignment.CENTER -> Layout.Alignment.ALIGN_CENTER
//                    TextAlignment.RIGHT -> Layout.Alignment.ALIGN_OPPOSITE
//                }
//                AlignmentSpan.Standard(alignment)
//            }
//        }
//
//        androidSpan?.let {
//            spannable.setSpan(it, styledSpan.start, styledSpan.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//        }
//    }
//
//    // 이미지는 비동기 로드가 필요하므로 콜백으로 처리
//    // 호출하는 쪽에서 이미지 위치(\uFFFC)를 찾아 로드 후 span 적용
//
//    return spannable
//}
//
///**
// * 텍스트에서 이미지 위치(\uFFFC) 인덱스 리스트 반환
// */
//fun RichTextContent.getImagePositions(): List<Int> {
//    val positions = mutableListOf<Int>()
//    text.forEachIndexed { index, char ->
//        if (char == RichTextContent.IMAGE_PLACEHOLDER) {
//            positions.add(index)
//        }
//    }
//    return positions
//}
