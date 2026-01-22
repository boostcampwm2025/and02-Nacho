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

fun NachoCard.toDto(): NachoCardDto {
    return NachoCardDto(
        id = id,
        content = content.toDto(),
        backgroundColor = backgroundColor,
        backgroundImageUrl = backgroundImageUrl,
    )
}

fun RichTextContent.toDto(): RichTextContentDto {
    return RichTextContentDto(
        text = text,
        spans = spans.map { it.toDto() },
        images = images,
    )
}

fun StyledSpan.toDto(): StyledSpanDto {
    return StyledSpanDto(
        start = start,
        end = end,
        style = style.toDto(),
    )
}

fun SpanStyle.toDto(): SpanStyleDto {
    return when (this) {
        is SpanStyle.Bold -> SpanStyleDto.Bold
        is SpanStyle.Italic -> SpanStyleDto.Italic
        is SpanStyle.Underline -> SpanStyleDto.Underline
        is SpanStyle.Strikethrough -> SpanStyleDto.Strikethrough
        is SpanStyle.FontSize -> SpanStyleDto.FontSize(size)
        is SpanStyle.TextColor -> SpanStyleDto.TextColor(color)
        is SpanStyle.BackgroundColor -> SpanStyleDto.BackgroundColor(color)
        is SpanStyle.Alignment -> SpanStyleDto.Alignment(alignment.toDto())
    }
}

fun TextAlignment.toDto(): TextAlignmentDto {
    return when (this) {
        TextAlignment.LEFT -> TextAlignmentDto.LEFT
        TextAlignment.CENTER -> TextAlignmentDto.CENTER
        TextAlignment.RIGHT -> TextAlignmentDto.RIGHT
    }
}
