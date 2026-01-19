package com.andlife.model.editor

import com.andlife.domain.model.card.NachoCard
import com.andlife.domain.model.card.RichTextContent
import com.andlife.domain.model.card.SpanStyle
import com.andlife.domain.model.card.StyledSpan
import com.andlife.domain.model.card.TextAlignment

fun NachoUiCard.toDomain(): NachoCard {
    require(allImagesUploaded) { "All images must be uploaded before converting to domain model" }
    return NachoCard(
        id = id,
        content = content.toDomain(),
        backgroundColor = backgroundColor,
        backgroundImageUrl = null,
    )
}

fun RichTextUiContent.toDomain(): RichTextContent {
    return RichTextContent(
        text = text,
        spans = spans.map { it.toDomain() },
        images = images.map { image ->
            require(image is CardImage.Remote) { "All images must be remote" }
            image.url
        },
    )
}

fun StyledUiSpan.toDomain(): StyledSpan {
    return StyledSpan(
        start = start,
        end = end,
        style = style.toDomain(),
    )
}

fun SpanUiStyle.toDomain(): SpanStyle {
    return when (this) {
        is SpanUiStyle.Bold -> SpanStyle.Bold
        is SpanUiStyle.Italic -> SpanStyle.Italic
        is SpanUiStyle.Underline -> SpanStyle.Underline
        is SpanUiStyle.Strikethrough -> SpanStyle.Strikethrough
        is SpanUiStyle.FontSize -> SpanStyle.FontSize(size)
        is SpanUiStyle.TextColor -> SpanStyle.TextColor(color)
        is SpanUiStyle.BackgroundColor -> SpanStyle.BackgroundColor(color)
        is SpanUiStyle.Alignment -> SpanStyle.Alignment(alignment.toDomain())
    }
}

fun TextUiAlignment.toDomain(): TextAlignment {
    return when (this) {
        TextUiAlignment.LEFT -> TextAlignment.LEFT
        TextUiAlignment.CENTER -> TextAlignment.CENTER
        TextUiAlignment.RIGHT -> TextAlignment.RIGHT
    }
}


fun NachoCard.toUiModel(): NachoUiCard {
    return NachoUiCard(
        id = id,
        content = content.toUiModel(),
        backgroundColor = backgroundColor,
    )
}

fun RichTextContent.toUiModel(): RichTextUiContent {
    return RichTextUiContent(
        text = text,
        spans = spans.map { it.toUiModel() },
        images = images.map { url -> CardImage.Remote(url) },
    )
}

fun StyledSpan.toUiModel(): StyledUiSpan {
    return StyledUiSpan(
        start = start,
        end = end,
        style = style.toUiModel(),
    )
}

fun SpanStyle.toUiModel(): SpanUiStyle {
    return when (this) {
        is SpanStyle.Bold -> SpanUiStyle.Bold
        is SpanStyle.Italic -> SpanUiStyle.Italic
        is SpanStyle.Underline -> SpanUiStyle.Underline
        is SpanStyle.Strikethrough -> SpanUiStyle.Strikethrough
        is SpanStyle.FontSize -> SpanUiStyle.FontSize(size)
        is SpanStyle.TextColor -> SpanUiStyle.TextColor(color)
        is SpanStyle.BackgroundColor -> SpanUiStyle.BackgroundColor(color)
        is SpanStyle.Alignment -> SpanUiStyle.Alignment(alignment.toUiModel())
    }
}

fun TextAlignment.toUiModel(): TextUiAlignment {
    return when (this) {
        TextAlignment.LEFT -> TextUiAlignment.LEFT
        TextAlignment.CENTER -> TextUiAlignment.CENTER
        TextAlignment.RIGHT -> TextUiAlignment.RIGHT
    }
}
