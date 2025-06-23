package com.dinarastepina.decomposedictionary.data.local.converter

import androidx.room.TypeConverter
import com.dinarastepina.decomposedictionary.data.local.serializer.JsonConfig
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject

/**
 * Generic base TypeConverter for Room database to handle JSON serialization/deserialization
 * of complex objects with custom serializers for inconsistent schema.
 * 
 * @param T The type of translation object (e.g., Translations, UlchiTranslation)
 */
abstract class BaseTypeConverter<T> {

    protected val json = JsonConfig.json

    /**
     * Serializer for the translation list - must be provided by subclasses
     */
    protected abstract val listSerializer: KSerializer<List<T>>

    /**
     * Serializer for individual translation objects - must be provided by subclasses
     */
    protected abstract val itemSerializer: KSerializer<T>

    /**
     * Converts a List<T> to JSON string for database storage.
     */
    @TypeConverter
    fun fromTranslationsList(translations: List<T>): String {
        return json.encodeToString(translations)
    }

    /**
     * Converts a JSON string to List<T> from database.
     * Handles both direct translations and full JSON word entries.
     */
    @TypeConverter
    fun toTranslationsList(translationsJson: String): List<T> {
        return try {
            // Parse the full JSON structure to extract just the translations part
            val jsonElement = Json.parseToJsonElement(translationsJson)
            val jsonObject = jsonElement.jsonObject
            
            // Extract the translations field specifically
            val translationsElement = jsonObject["translations"]
            
            if (translationsElement != null) {
                // Use our custom serializer to handle the translations field
                json.decodeFromJsonElement(listSerializer, translationsElement)
            } else {
                // If no translations field, treat the whole object as a single translation
                val singleTranslation = json.decodeFromString(itemSerializer, translationsJson)
                listOf(singleTranslation)
            }
        } catch (e: Exception) {
            // Fallback: return empty list if parsing fails
            emptyList()
        }
    }
} 