@file:OptIn(InternalSerializationApi::class)
package com.andlife.network.model.card

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NachoCardDto(
    val id: String? = null,
    val content: RichTextContentDto,
    val backgroundColor: Long,
    val backgroundImageUrl: String? = null,
)

@Serializable
data class RichTextContentDto(
    val text: String,
    val spans: List<StyledSpanDto> = emptyList(),
    val images: List<String> = emptyList(),
)

@Serializable
data class StyledSpanDto(
    val start: Int,
    val end: Int,
    val style: SpanStyleDto,
)

@Serializable
sealed interface SpanStyleDto {
    @Serializable
    @SerialName("bold")
    data object Bold : SpanStyleDto

    @Serializable
    @SerialName("italic")
    data object Italic : SpanStyleDto

    @Serializable
    @SerialName("underline")
    data object Underline : SpanStyleDto

    @Serializable
    @SerialName("strikethrough")
    data object Strikethrough : SpanStyleDto

    @Serializable
    @SerialName("fontSize")
    data class FontSize(val size: Float) : SpanStyleDto

    @Serializable
    @SerialName("textColor")
    data class TextColor(val color: Long) : SpanStyleDto

    @Serializable
    @SerialName("backgroundColor")
    data class BackgroundColor(val color: Long) : SpanStyleDto

    @Serializable
    @SerialName("alignment")
    data class Alignment(val alignment: TextAlignmentDto) : SpanStyleDto
}

@Serializable
enum class TextAlignmentDto {
    LEFT, CENTER, RIGHT
}
