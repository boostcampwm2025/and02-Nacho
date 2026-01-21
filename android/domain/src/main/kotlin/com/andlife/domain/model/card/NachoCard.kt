package com.andlife.domain.model.card

data class NachoCard(
    val id: String? = null,
    val content: RichTextContent,
    val backgroundColor: Long,
    val backgroundImageUrl: String?,
)

data class RichTextContent(
    val text: String,
    val spans: List<StyledSpan> = emptyList(),
    val images: List<String> = emptyList(),
)

data class StyledSpan(
    val start: Int,
    val end: Int,
    val style: SpanStyle,
)

sealed interface SpanStyle {

    data object Bold : SpanStyle

    data object Italic : SpanStyle

    data object Underline : SpanStyle

    data object Strikethrough : SpanStyle

    data class FontSize(
        val size: Float,
    ) : SpanStyle

    data class TextColor(
        val color: Long,
    ) : SpanStyle

    data class BackgroundColor(
        val color: Long,
    ) : SpanStyle

    data class Alignment(
        val alignment: TextAlignment,
    ) : SpanStyle
}

enum class TextAlignment {
    LEFT,
    CENTER,
    RIGHT,
}
