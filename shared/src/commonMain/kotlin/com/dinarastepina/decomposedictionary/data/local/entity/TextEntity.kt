package com.dinarastepina.decomposedictionary.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ulchi_texts")
data class TextEntity(
    @PrimaryKey
    val id: Int = 0,
    val ulchi: String,
    val russian: String,
    val audio: String,
)
