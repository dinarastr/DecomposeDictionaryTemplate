package ru.dinarastepina.ulchidictionary.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ulchi_phrases")
data class PhraseEntity(
    @PrimaryKey
    val id: Int = 0,
    val ulchi: String,
    val russian: String,
    val audio: String,
    val topicId: Int
)
