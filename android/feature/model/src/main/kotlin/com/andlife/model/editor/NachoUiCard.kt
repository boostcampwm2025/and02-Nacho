package com.andlife.model.editor

data class NachoUiCard(
    val id: String? = null,
    val content: RichTextUiContent,
    val backgroundColor: Long = DEFAULT_BACKGROUND_COLOR,
    val backgroundImageUrl: String? = null,
) {
    companion object {
        private const val DEFAULT_BACKGROUND_COLOR = 0xFFFFFFFF

        fun empty() = NachoUiCard(
            content = RichTextUiContent.empty()
        )
    }

    val allImagesUploaded: Boolean
        get() = content.images.all { it.isRemote }
}

data class RichTextUiContent(
    val text: String,
    val spans: List<StyledUiSpan> = emptyList(),
    val images: List<CardImage> = emptyList(),
) {
    companion object {
        const val IMAGE_PLACEHOLDER = '\uFFFC'

        fun empty() = RichTextUiContent("", emptyList(), emptyList())
    }

    val imagePlaceholderCount: Int
        get() = text.count { it == IMAGE_PLACEHOLDER }

    val isValid: Boolean
        get() = imagePlaceholderCount == images.size
}

sealed interface CardImage {
    data class Local(val uri: String) : CardImage

    data class Remote(val url: String) : CardImage

    val source: String
        get() = when (this) {
            is Local -> uri
            is Remote -> url
        }

    val isRemote: Boolean
        get() = this is Remote

    val isLocal: Boolean
        get() = this is Local
}

data class StyledUiSpan(
    val start: Int,
    val end: Int,
    val style: SpanUiStyle,
)

sealed interface SpanUiStyle {

    data object Bold : SpanUiStyle

    data object Italic : SpanUiStyle

    data object Underline : SpanUiStyle

    data object Strikethrough : SpanUiStyle

    data class FontSize(
        val size: Float,
    ) : SpanUiStyle

    data class TextColor(
        val color: Long,
    ) : SpanUiStyle

    data class BackgroundColor(
        val color: Long,
    ) : SpanUiStyle

    data class Alignment(
        val alignment: TextUiAlignment,
    ) : SpanUiStyle
}

enum class TextUiAlignment {
    LEFT,
    CENTER,
    RIGHT,
}
