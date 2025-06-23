package com.dinarastepina.decomposedictionary.data.local.converter

import androidx.room.TypeConverter
import com.dinarastepina.decomposedictionary.data.local.entity.Translations
import com.dinarastepina.decomposedictionary.data.local.entity.UlchiTranslation
import com.dinarastepina.decomposedictionary.data.local.entity.UlchiTranslationsListSerializer
import com.dinarastepina.decomposedictionary.data.local.serializer.JsonConfig
import com.dinarastepina.decomposedictionary.data.local.serializer.TranslationsListSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject

class UlchiTypeConverter {

    private val json = JsonConfig.json

    /**
     * Converts a List<Translations> to JSON string for database storage.
     */
    @TypeConverter
    fun fromTranslationsList(translations: List<UlchiTranslation>): String {
        return json.encodeToString(translations)
    }

    /**
     * Converts a JSON string to List<Translations> from database.
     * Handles both direct translations and full JSON word entries.
     */
    @TypeConverter
    fun toTranslationsList(translationsJson: String): List<UlchiTranslation> {
        return try {
            // Parse the full JSON structure to extract just the translations part
            val jsonElement = Json.parseToJsonElement(translationsJson)
            val jsonObject = jsonElement.jsonObject

            // Extract the translations field specifically
            val translationsElement = jsonObject["translations"]

            if (translationsElement != null) {
                // Use our TranslationsListSerializer to handle the translations field
                json.decodeFromJsonElement(UlchiTranslationsListSerializer, translationsElement)
            } else {
                // If no translations field, treat the whole object as a single translation
                val singleTranslation = json.decodeFromString<UlchiTranslation>(translationsJson)
                listOf(singleTranslation)
            }
        } catch (e: Exception) {
            // Fallback: return empty list if parsing fails
            emptyList()
        }
    }
}