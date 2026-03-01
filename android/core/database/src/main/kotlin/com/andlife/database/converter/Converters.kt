package com.andlife.database.converter

import androidx.room.TypeConverter
import com.andlife.database.entity.MediaCache
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun fromMediaList(value: List<MediaCache>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toMediaList(value: String): List<MediaCache> {
        return Json.decodeFromString(value)
    }
}
