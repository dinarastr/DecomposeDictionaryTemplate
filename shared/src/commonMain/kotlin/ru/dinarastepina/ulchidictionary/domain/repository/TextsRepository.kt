package ru.dinarastepina.ulchidictionary.domain.repository

import ru.dinarastepina.ulchidictionary.domain.model.Text

interface TextsRepository {
    suspend fun fetchAllTexts(): List<Text>
}