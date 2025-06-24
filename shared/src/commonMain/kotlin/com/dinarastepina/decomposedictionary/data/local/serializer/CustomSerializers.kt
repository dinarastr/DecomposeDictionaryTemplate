package com.dinarastepina.decomposedictionary.data.local.serializer

import com.dinarastepina.decomposedictionary.data.local.entity.Acronym
import com.dinarastepina.decomposedictionary.data.local.entity.Definition
import com.dinarastepina.decomposedictionary.data.local.entity.UlchiDefinition
import com.dinarastepina.decomposedictionary.data.local.entity.UlchiTranslation
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive

object UlchiTranslationsListSerializer : KSerializer<List<UlchiTranslation>> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("UlchiTranslationsList")

    override fun serialize(encoder: Encoder, value: List<UlchiTranslation>) {
        encoder.encodeSerializableValue(JsonArray.serializer(), JsonArray(value.map {
            JsonConfig.json.encodeToJsonElement(UlchiTranslation.serializer(), it)
        }))
    }

    override fun deserialize(decoder: Decoder): List<UlchiTranslation> {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())

        return when (jsonElement) {
            is JsonObject -> {
                listOf(JsonConfig.json.decodeFromJsonElement(UlchiTranslation.serializer(), jsonElement))
            }
            is JsonArray -> {
                jsonElement.map {
                    JsonConfig.json.decodeFromJsonElement(UlchiTranslation.serializer(), it)
                }
            }
            else -> emptyList()
        }
    }
}


object TranslationsListSerializer : KSerializer<List<com.dinarastepina.decomposedictionary.data.local.entity.Translations>> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("TranslationsList")

    override fun serialize(encoder: Encoder, value: List<com.dinarastepina.decomposedictionary.data.local.entity.Translations>) {
        encoder.encodeSerializableValue(JsonArray.serializer(), JsonArray(value.map { 
            JsonConfig.json.encodeToJsonElement(com.dinarastepina.decomposedictionary.data.local.entity.Translations.serializer(), it) 
        }))
    }

    override fun deserialize(decoder: Decoder): List<com.dinarastepina.decomposedictionary.data.local.entity.Translations> {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        
        return when (jsonElement) {
            is JsonObject -> {
                listOf(JsonConfig.json.decodeFromJsonElement(com.dinarastepina.decomposedictionary.data.local.entity.Translations.serializer(), jsonElement))
            }
            is JsonArray -> {
                jsonElement.map {
                    JsonConfig.json.decodeFromJsonElement(com.dinarastepina.decomposedictionary.data.local.entity.Translations.serializer(), it) 
                }
            }
            else -> emptyList()
        }
    }
}

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
                try {
                    val acronym = JsonConfig.json.decodeFromJsonElement(Acronym.serializer(), jsonElement)
                    acronym.text
                } catch (e: Exception) {
                    jsonElement["text"]?.jsonPrimitive?.content
                        ?: jsonElement["title"]?.jsonPrimitive?.content 
                        ?: jsonElement.toString()
                }
            }
            else -> jsonElement.toString()
        }
    }
}

object AcronymListSerializer : KSerializer<List<Acronym>> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("AcronymList")

    override fun serialize(encoder: Encoder, value: List<Acronym>) {
        encoder.encodeSerializableValue(JsonArray.serializer(), JsonArray(value.map { 
            JsonConfig.json.encodeToJsonElement(Acronym.serializer(), it) 
        }))
    }

    override fun deserialize(decoder: Decoder): List<Acronym> {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        
        return when (jsonElement) {
            is JsonArray -> {
                jsonElement.map {
                    JsonConfig.json.decodeFromJsonElement(Acronym.serializer(), it) 
                }
            }
            is JsonObject -> {
                listOf(JsonConfig.json.decodeFromJsonElement(Acronym.serializer(), jsonElement))
            }
            else -> emptyList()
        }
    }
}

object DefinitionListSerializer : KSerializer<List<Definition>> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("DefinitionList")

    override fun serialize(encoder: Encoder, value: List<Definition>) {
        encoder.encodeSerializableValue(JsonArray.serializer(), JsonArray(value.map { 
            JsonConfig.json.encodeToJsonElement(Definition.serializer(), it) 
        }))
    }

    override fun deserialize(decoder: Decoder): List<Definition> {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        
        return when (jsonElement) {
            is JsonArray -> {
                jsonElement.mapNotNull { element ->
                    when (element) {
                        is JsonPrimitive -> {
                            if (element.isString) {
                                Definition(com = null, text = element.content)
                            } else null
                        }
                        is JsonObject -> {
                            JsonConfig.json.decodeFromJsonElement(Definition.serializer(), element)
                        }
                        else -> null
                    }
                }
            }
            is JsonObject -> {
                listOf(JsonConfig.json.decodeFromJsonElement(Definition.serializer(), jsonElement))
            }
            is JsonPrimitive -> {
                if (jsonElement.isString) {
                    listOf(Definition(com = null, text = jsonElement.content))
                } else emptyList()
            }
        }
    }
}

object UlchiDefinitionListSerializer : KSerializer<List<UlchiDefinition>> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("UlchiDefinitionList")

    override fun serialize(encoder: Encoder, value: List<UlchiDefinition>) {
        encoder.encodeSerializableValue(JsonArray.serializer(), JsonArray(value.map {
            JsonConfig.json.encodeToJsonElement(UlchiDefinition.serializer(), it)
        }))
    }

    override fun deserialize(decoder: Decoder): List<UlchiDefinition> {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())

        return when (jsonElement) {
            is JsonArray -> {
                jsonElement.mapNotNull { element ->
                    when (element) {
                        is JsonPrimitive -> {
                            if (element.isString) {
                                UlchiDefinition(com = null, text = element.content)
                            } else null
                        }
                        is JsonObject -> {
                            JsonConfig.json.decodeFromJsonElement(UlchiDefinition.serializer(), element)
                        }
                        else -> null
                    }
                }
            }
            is JsonObject -> {
                listOf(JsonConfig.json.decodeFromJsonElement(UlchiDefinition.serializer(), jsonElement))
            }
            is JsonPrimitive -> {
                if (jsonElement.isString) {
                    listOf(UlchiDefinition(com = null, text = jsonElement.content))
                } else emptyList()
            }
        }
    }
}

object ExampleListSerializer : KSerializer<List<com.dinarastepina.decomposedictionary.data.local.entity.Example>> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ExampleList")

    override fun serialize(encoder: Encoder, value: List<com.dinarastepina.decomposedictionary.data.local.entity.Example>) {
        encoder.encodeSerializableValue(JsonArray.serializer(), JsonArray(value.map { 
            JsonConfig.json.encodeToJsonElement(com.dinarastepina.decomposedictionary.data.local.entity.Example.serializer(), it) 
        }))
    }

    override fun deserialize(decoder: Decoder): List<com.dinarastepina.decomposedictionary.data.local.entity.Example> {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        
        return when (jsonElement) {
            is JsonArray -> {
                jsonElement.map {
                    JsonConfig.json.decodeFromJsonElement(com.dinarastepina.decomposedictionary.data.local.entity.Example.serializer(), it) 
                }
            }
            is JsonObject -> {
                listOf(JsonConfig.json.decodeFromJsonElement(com.dinarastepina.decomposedictionary.data.local.entity.Example.serializer(), jsonElement))
            }
            else -> emptyList()
        }
    }
} 