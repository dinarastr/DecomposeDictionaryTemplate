package ru.dinarastepina.ulchidictionary.domain.model

data class Phrase(
    val id: Int = 0,
    val ulchi: String,
    val russian: String,
    val audio: String,
    val topicId: Int
)
