package com.dinarastepina.decomposedictionary.data.local.converter

import androidx.room.TypeConverter
import com.dinarastepina.decomposedictionary.data.local.entity.JsonWordEntry
import com.dinarastepina.decomposedictionary.data.local.entity.Translations
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * TypeConverter for Room database to handle JSON serialization/deserialization
 * of complex objects used in WordEntity with custom serializers for inconsistent schema.
 */
class WordTypeConverter {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = false
        coerceInputValues = true
        allowStructuredMapKeys = true
    }

    /**
     * Converts a Translations object to JSON string for database storage.
     */
    @TypeConverter
    fun fromTranslations(translations: Translations): String {
        return json.encodeToString(translations)
    }

    /**
     * Converts a JSON string to Translations object from database.
     * Handles both direct translations and full JSON word entries.
     */
    @TypeConverter
    fun toTranslations(translationsJson: String): Translations {
        return try {
            // First try to parse as a full JsonWordEntry (handles both formats)
            val wordEntry = json.decodeFromString<JsonWordEntry>(translationsJson)
            wordEntry.getNormalizedTranslations()
        } catch (e: Exception) {
            try {
                // Fallback: try to parse as direct Translations object
                json.decodeFromString<Translations>(translationsJson)
            } catch (e2: Exception) {
                println("Error parsing translations JSON: ${e.message}")
                println("Fallback error: ${e2.message}")
                println("JSON content: $translationsJson")
                // Return empty translations object as fallback
                Translations()
            }
        }
    }
} 