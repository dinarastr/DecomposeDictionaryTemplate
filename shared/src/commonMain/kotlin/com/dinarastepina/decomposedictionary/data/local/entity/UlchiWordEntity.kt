package com.dinarastepina.decomposedictionary.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dinarastepina.decomposedictionary.data.local.serializer.ExampleListSerializer
import com.dinarastepina.decomposedictionary.data.local.serializer.UlchiDefinitionListSerializer
import kotlinx.serialization.Serializable

@Entity(tableName = "ulchi_russian")
data class UlchiWordEntity(
    @PrimaryKey
    val id: Int = 0,
    val word: String,
    val grammar: String? = null,
    @Serializable(with = UlchiTranslationsListSerializer::class)
    val translations: List<UlchiTranslation>
)

@Serializable
data class UlchiTranslation(
    @Serializable(with = UlchiDefinitionListSerializer::class)
    val definition: List<UlchiDefinition> = emptyList(),
    @Serializable(with = ExampleListSerializer::class)
    val examples: List<Example> = emptyList(),
    val text: String? = null
)


@Serializable
data class UlchiDefinition(
    val com: String? = null,
    val text: String,
)