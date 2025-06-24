package com.dinarastepina.decomposedictionary.data.repository

import com.dinarastepina.decomposedictionary.data.local.dao.TextsDao
import com.dinarastepina.decomposedictionary.data.mapper.toDomain
import com.dinarastepina.decomposedictionary.domain.model.Text
import com.dinarastepina.decomposedictionary.domain.repository.TextsRepository

class TextsRepositoryImpl(
    private val textDao: TextsDao
): TextsRepository {

    override suspend fun fetchAllTexts(): List<Text> {
        return textDao.getAllTexts().map {
            it.toDomain()
        }
    }
}