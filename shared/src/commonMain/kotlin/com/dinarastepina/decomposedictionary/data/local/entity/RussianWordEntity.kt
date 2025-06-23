package com.dinarastepina.decomposedictionary.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dinarastepina.decomposedictionary.data.local.serializer.AcronymListSerializer
import com.dinarastepina.decomposedictionary.data.local.serializer.DefinitionListSerializer
import com.dinarastepina.decomposedictionary.data.local.serializer.ExampleListSerializer
import com.dinarastepina.decomposedictionary.data.local.serializer.ExampleTranslationSerializer
import com.dinarastepina.decomposedictionary.data.local.serializer.JsonConfig
import com.dinarastepina.decomposedictionary.data.local.serializer.TranslationsListSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

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
                // It's a single Translations object, wrap it in a list
                listOf(JsonConfig.json.decodeFromJsonElement(UlchiTranslation.serializer(), jsonElement))
            }
            is JsonArray -> {
                // It's an array of Translations objects - preserve them separately
                jsonElement.map {
                    JsonConfig.json.decodeFromJsonElement(UlchiTranslation.serializer(), it)
                }
            }
            else -> emptyList()
        }
    }
}

/**
 * Room entity representing a word in the local database.
 */
@Entity(tableName = "russian_ulchi")
data class RussianWordEntity(
    @PrimaryKey
    val id: Int = 0,
    val word: String,
    @Serializable(with = TranslationsListSerializer::class)
    val translations: List<Translations>
)

@Serializable
data class Translations(
    @Serializable(with = AcronymListSerializer::class)
    val acronym: List<Acronym> = emptyList(),
    val pre: String? = null,
    @Serializable(with = DefinitionListSerializer::class)
    val definition: List<Definition> = emptyList(),
    @Serializable(with = ExampleListSerializer::class)
    val example: List<Example> = emptyList(),
    val grammar: Grammar? = null,
    val text: String? = null,
    val com: String? = null // Comment field that can appear in array elements
)

@Serializable
data class Acronym(
    val title: String,
    val text: String
)

@Serializable
data class Example(
    val ex: String,
    @SerialName("ex_tr")
    @Serializable(with = ExampleTranslationSerializer::class)
    val exTr: String
)

@Serializable
data class Definition(
    val com: String? = null,
    val text: String,
    val note: String? = null
)

@Serializable
data class Grammar(
    val acronym: List<Acronym>? = null,
    val text: String
)