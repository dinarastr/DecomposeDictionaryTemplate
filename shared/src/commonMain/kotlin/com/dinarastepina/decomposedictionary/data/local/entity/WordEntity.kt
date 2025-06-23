package com.dinarastepina.decomposedictionary.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dinarastepina.decomposedictionary.data.local.serializer.AcronymListSerializer
import com.dinarastepina.decomposedictionary.data.local.serializer.DefinitionListSerializer
import com.dinarastepina.decomposedictionary.data.local.serializer.ExampleListSerializer
import com.dinarastepina.decomposedictionary.data.local.serializer.ExampleTranslationSerializer
import com.dinarastepina.decomposedictionary.data.local.serializer.TranslationsListSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Room entity representing a word in the local database.
 */
@Entity(tableName = "russian_ulchi")
data class WordEntity(
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

// For the complete JSON entry structure
@Serializable
data class JsonWordEntry(
    val word: String,
    @Serializable(with = TranslationsListSerializer::class)
    val translations: List<Translations>
)