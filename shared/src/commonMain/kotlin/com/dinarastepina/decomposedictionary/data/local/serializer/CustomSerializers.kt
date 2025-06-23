package com.dinarastepina.decomposedictionary.data.local.serializer

import com.dinarastepina.decomposedictionary.data.local.entity.Acronym
import com.dinarastepina.decomposedictionary.data.local.entity.Definition
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

/**
 * Custom serializer for the entire translations field that can be either:
 * - A single Translations object
 * - An array of Translations objects
 * Always returns a List<Translations> preserving semantic separation
 */
object TranslationsListSerializer : KSerializer<List<com.dinarastepina.decomposedictionary.data.local.entity.Translations>> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("TranslationsList")

    override fun serialize(encoder: Encoder, value: List<com.dinarastepina.decomposedictionary.data.local.entity.Translations>) {
        encoder.encodeSerializableValue(JsonArray.serializer(), JsonArray(value.map { 
            Json.encodeToJsonElement(com.dinarastepina.decomposedictionary.data.local.entity.Translations.serializer(), it) 
        }))
    }

    override fun deserialize(decoder: Decoder): List<com.dinarastepina.decomposedictionary.data.local.entity.Translations> {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        
        return when (jsonElement) {
            is JsonObject -> {
                // It's a single Translations object, wrap it in a list
                listOf(Json.decodeFromJsonElement(com.dinarastepina.decomposedictionary.data.local.entity.Translations.serializer(), jsonElement))
            }
            is JsonArray -> {
                // It's an array of Translations objects - preserve them separately
                jsonElement.map { 
                    Json.decodeFromJsonElement(com.dinarastepina.decomposedictionary.data.local.entity.Translations.serializer(), it) 
                }
            }
            else -> emptyList()
        }
    }
}

/**
 * Custom serializer for ex_tr field in Example that can be either:
 * - A simple string
 * - An Acronym object
 * Always returns a string for consistency
 */
object ExampleTranslationSerializer : KSerializer<String> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ExampleTranslation")

    override fun serialize(encoder: Encoder, value: String) {
        encoder.encodeString(value)
    }

    override fun deserialize(decoder: Decoder): String {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        
        return when (jsonElement) {
            is JsonPrimitive -> {
                if (jsonElement.isString) {
                    jsonElement.content
                } else jsonElement.toString()
            }
            is JsonObject -> {
                // Try to decode as Acronym and extract text, fallback to title
                try {
                    val acronym = Json.decodeFromJsonElement(Acronym.serializer(), jsonElement)
                    acronym.text
                } catch (e: Exception) {
                    // Fallback: try to get any text field
                    jsonElement["text"]?.jsonPrimitive?.content 
                        ?: jsonElement["title"]?.jsonPrimitive?.content 
                        ?: jsonElement.toString()
                }
            }
            else -> jsonElement.toString()
        }
    }
}

/**
 * Custom serializer for acronym field that can be either:
 * - A single Acronym object
 * - An array of Acronym objects
 * Always returns a List<Acronym> for consistency
 */
object AcronymListSerializer : KSerializer<List<Acronym>> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("AcronymList")

    override fun serialize(encoder: Encoder, value: List<Acronym>) {
        encoder.encodeSerializableValue(JsonArray.serializer(), JsonArray(value.map { 
            Json.encodeToJsonElement(Acronym.serializer(), it) 
        }))
    }

    override fun deserialize(decoder: Decoder): List<Acronym> {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        
        return when (jsonElement) {
            is JsonArray -> {
                // It's already an array
                jsonElement.map { 
                    Json.decodeFromJsonElement(Acronym.serializer(), it) 
                }
            }
            is JsonObject -> {
                // It's a single object, wrap it in a list
                listOf(Json.decodeFromJsonElement(Acronym.serializer(), jsonElement))
            }
            else -> emptyList()
        }
    }
}

/**
 * Custom serializer for definition field that can be either:
 * - A simple string
 * - A Definition object with com and text fields
 * - An array of Definition objects
 * Always returns a List<Definition> for consistency
 */
object DefinitionListSerializer : KSerializer<List<Definition>> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("DefinitionList")

    override fun serialize(encoder: Encoder, value: List<Definition>) {
        encoder.encodeSerializableValue(JsonArray.serializer(), JsonArray(value.map { 
            Json.encodeToJsonElement(Definition.serializer(), it) 
        }))
    }

    override fun deserialize(decoder: Decoder): List<Definition> {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        
        return when (jsonElement) {
            is JsonArray -> {
                // It's an array, decode each element
                jsonElement.mapNotNull { element ->
                    when (element) {
                        is JsonPrimitive -> {
                            if (element.isString) {
                                Definition(com = null, text = element.content)
                            } else null
                        }
                        is JsonObject -> {
                            Json.decodeFromJsonElement(Definition.serializer(), element)
                        }
                        else -> null
                    }
                }
            }
            is JsonObject -> {
                // It's a single object
                listOf(Json.decodeFromJsonElement(Definition.serializer(), jsonElement))
            }
            is JsonPrimitive -> {
                // It's a simple string
                if (jsonElement.isString) {
                    listOf(Definition(com = null, text = jsonElement.content))
                } else emptyList()
            }
        }
    }
}

/**
 * Custom serializer for example field that can be either:
 * - A single Example object
 * - An array of Example objects
 * Always returns a List<Example> for consistency
 */
object ExampleListSerializer : KSerializer<List<com.dinarastepina.decomposedictionary.data.local.entity.Example>> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ExampleList")

    override fun serialize(encoder: Encoder, value: List<com.dinarastepina.decomposedictionary.data.local.entity.Example>) {
        encoder.encodeSerializableValue(JsonArray.serializer(), JsonArray(value.map { 
            Json.encodeToJsonElement(com.dinarastepina.decomposedictionary.data.local.entity.Example.serializer(), it) 
        }))
    }

    override fun deserialize(decoder: Decoder): List<com.dinarastepina.decomposedictionary.data.local.entity.Example> {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        
        return when (jsonElement) {
            is JsonArray -> {
                // It's already an array
                jsonElement.map { 
                    Json.decodeFromJsonElement(com.dinarastepina.decomposedictionary.data.local.entity.Example.serializer(), it) 
                }
            }
            is JsonObject -> {
                // It's a single object, wrap it in a list
                listOf(Json.decodeFromJsonElement(com.dinarastepina.decomposedictionary.data.local.entity.Example.serializer(), jsonElement))
            }
            else -> emptyList()
        }
    }
} 