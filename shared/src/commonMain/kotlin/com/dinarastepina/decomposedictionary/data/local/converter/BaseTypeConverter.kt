package com.dinarastepina.decomposedictionary.data.local.converter

import androidx.room.TypeConverter
import com.dinarastepina.decomposedictionary.data.local.serializer.JsonConfig
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject

abstract class BaseTypeConverter<T> {

    protected val json = JsonConfig.json

    protected abstract val listSerializer: KSerializer<List<T>>

    protected abstract val itemSerializer: KSerializer<T>

    @TypeConverter
    fun fromTranslationsList(translations: List<T>): String {
        return json.encodeToString(translations)
    }

    @TypeConverter
    fun toTranslationsList(translationsJson: String): List<T> {
        return try {
            val jsonElement = Json.parseToJsonElement(translationsJson)
            val jsonObject = jsonElement.jsonObject
            
            val translationsElement = jsonObject["translations"]
            
            if (translationsElement != null) {
                json.decodeFromJsonElement(listSerializer, translationsElement)
            } else {
                val singleTranslation = json.decodeFromString(itemSerializer, translationsJson)
                listOf(singleTranslation)
            }
        } catch (e: Exception) {
            println("Error parsing translations: ${e.message}")
            println("JSON input: $translationsJson")
            emptyList()
        }
    }
} 