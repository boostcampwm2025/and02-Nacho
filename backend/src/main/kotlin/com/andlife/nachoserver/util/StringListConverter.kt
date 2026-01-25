package com.andlife.nachoserver.util

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class StringListConverter : AttributeConverter<List<String>, String> {

    private val splitChar = ","

    // 엔티티의 List를 DB에 넣을 String으로 변환 (List -> "url1,url2")
    override fun convertToDatabaseColumn(attribute: List<String>?): String? {
        return attribute?.joinToString(splitChar)
    }

    // DB에서 가져온 String을 엔티티의 List로 변환 ("url1,url2" -> List)
    override fun convertToEntityAttribute(dbData: String?): List<String> {
        return dbData?.split(splitChar)?.filter { it.isNotBlank() } ?: emptyList()
    }

}