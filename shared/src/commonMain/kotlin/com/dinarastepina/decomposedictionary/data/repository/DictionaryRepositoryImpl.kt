package com.dinarastepina.decomposedictionary.data.repository

import com.dinarastepina.decomposedictionary.data.local.dao.RussianDao
import com.dinarastepina.decomposedictionary.data.mapper.toDomain
import com.dinarastepina.decomposedictionary.domain.model.Word
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementation of DictionaryRepository using Room database.
 */
class DictionaryRepositoryImpl(
    private val russianDao: RussianDao,
) : DictionaryRepository {
    
    override fun getAllWords(pageSize: Int, offset: Int): Flow<List<Word>> {
        return russianDao.getAllWords(pageSize, offset).map { entities ->
            entities.map { entity ->
                entity.toDomain()
            }
        }
    }
    
    override fun searchWords(query: String, pageSize: Int, offset: Int): Flow<List<Word>> {
        return russianDao.searchWords(query, pageSize, offset).map { entities ->
            entities.map { entity ->
                entity.toDomain()
            }
        }
    }
    
    override suspend fun getWordsCount(): Int {
        return russianDao.getWordsCount()
    }
    
    override suspend fun getSearchResultsCount(query: String): Int {
        return russianDao.getSearchResultsCount(query)
    }
} 