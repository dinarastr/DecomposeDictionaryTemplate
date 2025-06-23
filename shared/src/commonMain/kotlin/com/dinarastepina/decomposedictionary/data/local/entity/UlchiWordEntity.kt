package com.dinarastepina.decomposedictionary.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dinarastepina.decomposedictionary.data.local.serializer.DefinitionListSerializer
import com.dinarastepina.decomposedictionary.data.local.serializer.ExampleListSerializer
import kotlinx.serialization.Serializable

@Entity(tableName = "ulchi_russian")
data class UlchiWordEntity(
    @PrimaryKey
    val id: Int = 0,
    val word: String,
    val grammar: String?,
    @Serializable(with = UlchiTranslationsListSerializer::class)
    val translations: List<Translations>
)

@Serializable
data class UlchiTranslation(
    @Serializable(with = DefinitionListSerializer::class)
    val definition: List<Definition> = emptyList(),
    @Serializable(with = ExampleListSerializer::class)
    val examples: List<Example> = emptyList()
)
