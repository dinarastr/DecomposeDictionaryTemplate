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
                try {
                    listOf(JsonConfig.json.decodeFromJsonElement(com.dinarastepina.decomposedictionary.data.local.entity.Translations.serializer(), jsonElement))
                } catch (e: Exception) {
                    println("Error parsing Translations object: ${e.message}")
                    println("JSON input: $jsonElement")
                    emptyList()
                }
            }
            is JsonArray -> {
                jsonElement.mapNotNull { element ->
                    try {
                        JsonConfig.json.decodeFromJsonElement(com.dinarastepina.decomposedictionary.data.local.entity.Translations.serializer(), element)
                    } catch (e: Exception) {
                        println("Error parsing Translations array element: ${e.message}")
                        println("JSON input: $element")
                        null
                    }
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
                jsonElement.mapNotNull { element ->
                    try {
                        JsonConfig.json.decodeFromJsonElement(Acronym.serializer(), element)
                    } catch (e: Exception) {
                        println("Error parsing acronym element: ${e.message}")
                        println("JSON input: $element")
                        null
                    }
                }
            }
            is JsonObject -> {
                try {
                    listOf(JsonConfig.json.decodeFromJsonElement(Acronym.serializer(), jsonElement))
                } catch (e: Exception) {
                    println("Error parsing acronym object: ${e.message}")
                    println("JSON input: $jsonElement")
                    emptyList()
                }
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
                            try {
                                JsonConfig.json.decodeFromJsonElement(Definition.serializer(), element)
                            } catch (e: Exception) {
                                // Handle case where 'com' field is an array instead of string
                                val com = element["com"]?.let { comElement ->
                                    when (comElement) {
                                        is JsonArray -> comElement.joinToString(", ") { 
                                            it.jsonPrimitive.content 
                                        }
                                        is JsonPrimitive -> comElement.content
                                        else -> null
                                    }
                                }
                                val text = element["text"]?.jsonPrimitive?.content ?: ""
                                val note = element["note"]?.jsonPrimitive?.content
                                Definition(com = com, text = text, note = note)
                            }
                        }
                        else -> null
                    }
                }
            }
            is JsonObject -> {
                try {
                    listOf(JsonConfig.json.decodeFromJsonElement(Definition.serializer(), jsonElement))
                } catch (e: Exception) {
                    println("Error parsing Definition object, attempting manual parsing: ${e.message}")
                    println("JSON input: $jsonElement")
                    
                    // Handle case where 'com' field is an array instead of string
                    val com = jsonElement["com"]?.let { comElement ->
                        when (comElement) {
                            is JsonArray -> comElement.joinToString(", ") { 
                                it.jsonPrimitive.content 
                            }
                            is JsonPrimitive -> comElement.content
                            else -> null
                        }
                    }
                    val text = jsonElement["text"]?.jsonPrimitive?.content ?: ""
                    val note = jsonElement["note"]?.jsonPrimitive?.content
                    listOf(Definition(com = com, text = text, note = note))
                }
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

object GrammarSerializer : KSerializer<com.dinarastepina.decomposedictionary.data.local.entity.Grammar> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Grammar")

    override fun serialize(encoder: Encoder, value: com.dinarastepina.decomposedictionary.data.local.entity.Grammar) {
        encoder.encodeSerializableValue(com.dinarastepina.decomposedictionary.data.local.entity.Grammar.serializer(), value)
    }

    override fun deserialize(decoder: Decoder): com.dinarastepina.decomposedictionary.data.local.entity.Grammar {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        
        return when (jsonElement) {
            is JsonObject -> {
                try {
                    JsonConfig.json.decodeFromJsonElement(com.dinarastepina.decomposedictionary.data.local.entity.Grammar.serializer(), jsonElement)
                } catch (e: Exception) {
                    println("Error parsing Grammar object, attempting manual parsing: ${e.message}")
                    println("JSON input: $jsonElement")
                    
                    // Handle case where 'acronym' field is a single object instead of array
                    val acronym = jsonElement["acronym"]?.let { acronymElement ->
                        when (acronymElement) {
                            is JsonArray -> {
                                acronymElement.mapNotNull { element ->
                                    try {
                                        JsonConfig.json.decodeFromJsonElement(Acronym.serializer(), element)
                                    } catch (ex: Exception) {
                                        println("Error parsing acronym in array: ${ex.message}")
                                        null
                                    }
                                }
                            }
                            is JsonObject -> {
                                try {
                                    listOf(JsonConfig.json.decodeFromJsonElement(Acronym.serializer(), acronymElement))
                                } catch (ex: Exception) {
                                    println("Error parsing single acronym object: ${ex.message}")
                                    emptyList()
                                }
                            }
                            else -> null
                        }
                    }
                    val text = jsonElement["text"]?.jsonPrimitive?.content ?: ""
                    com.dinarastepina.decomposedictionary.data.local.entity.Grammar(acronym = acronym, text = text)
                }
            }
            else -> throw IllegalArgumentException("Expected JsonObject for Grammar, got ${jsonElement::class.simpleName}")
        }
    }
} 