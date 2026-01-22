package com.andlife.data.repository.invitation.mapper

import com.andlife.domain.model.card.NachoCard
import com.andlife.domain.model.card.RichTextContent
import com.andlife.domain.model.card.SpanStyle
import com.andlife.domain.model.card.StyledSpan
import com.andlife.domain.model.card.TextAlignment
import com.andlife.network.model.card.NachoCardDto
import com.andlife.network.model.card.RichTextContentDto
import com.andlife.network.model.card.SpanStyleDto
import com.andlife.network.model.card.StyledSpanDto
import com.andlife.network.model.card.TextAlignmentDto

fun NachoCardDto.toDomain(): NachoCard {
    return NachoCard(
        id = id,
        content = content.toDomain(),
        backgroundColor = backgroundColor,
        backgroundImageUrl = backgroundImageUrl,
    )
}

fun RichTextContentDto.toDomain(): RichTextContent {
    return RichTextContent(
        text = text,
        spans = spans.map { it.toDomain() },
        images = images,
    )
}

fun StyledSpanDto.toDomain(): StyledSpan {
    return StyledSpan(
        start = start,
        end = end,
        style = style.toDomain(),
    )
}

fun SpanStyleDto.toDomain(): SpanStyle {
    return when (this) {
        is SpanStyleDto.Bold -> SpanStyle.Bold
        is SpanStyleDto.Italic -> SpanStyle.Italic
        is SpanStyleDto.Underline -> SpanStyle.Underline
        is SpanStyleDto.Strikethrough -> SpanStyle.Strikethrough
        is SpanStyleDto.FontSize -> SpanStyle.FontSize(size)
        is SpanStyleDto.TextColor -> SpanStyle.TextColor(color)
        is SpanStyleDto.BackgroundColor -> SpanStyle.BackgroundColor(color)
        is SpanStyleDto.Alignment -> SpanStyle.Alignment(alignment.toDomain())
    }
}

fun TextAlignmentDto.toDomain(): TextAlignment {
    return when (this) {
        TextAlignmentDto.LEFT -> TextAlignment.LEFT
        TextAlignmentDto.CENTER -> TextAlignment.CENTER
        TextAlignmentDto.RIGHT -> TextAlignment.RIGHT
    }
}
