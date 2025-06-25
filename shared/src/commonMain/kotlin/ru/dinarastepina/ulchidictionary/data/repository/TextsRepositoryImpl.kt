package ru.dinarastepina.ulchidictionary.data.repository

import ru.dinarastepina.ulchidictionary.data.local.dao.TextsDao
import ru.dinarastepina.ulchidictionary.data.mapper.toDomain
import ru.dinarastepina.ulchidictionary.domain.model.Text
import ru.dinarastepina.ulchidictionary.domain.repository.TextsRepository

class TextsRepositoryImpl(
    private val textDao: TextsDao
): TextsRepository {

    override suspend fun fetchAllTexts(): List<Text> {
        return textDao.getAllTexts().map {
            it.toDomain()
        }
    }
}