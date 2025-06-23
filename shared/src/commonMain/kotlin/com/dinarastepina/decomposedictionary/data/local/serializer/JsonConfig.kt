package com.dinarastepina.decomposedictionary.data.local.serializer

import kotlinx.serialization.json.Json

/**
 * Singleton Json configuration used throughout the app for consistent serialization.
 */
object JsonConfig {
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = false
        coerceInputValues = true
        allowStructuredMapKeys = true
    }
} 