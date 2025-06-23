package com.dinarastepina.decomposedictionary.data.repository

import com.dinarastepina.decomposedictionary.data.local.dao.RussianDao
import com.dinarastepina.decomposedictionary.data.local.dao.UlchiDao
import com.dinarastepina.decomposedictionary.data.mapper.toDomain
import com.dinarastepina.decomposedictionary.domain.model.RussianWord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DictionaryRepositoryImpl(
    private val russianDao: RussianDao,
    private val ulchiDao: UlchiDao
) : DictionaryRepository {
    
    override fun getAllRussianWords(pageSize: Int, offset: Int): Flow<List<RussianWord>> {
        return russianDao.getAllWords(pageSize, offset).map { entities ->
            entities.map { entity ->
                println(entity.toString())
                entity.toDomain()
            }
        }
    }

    override fun getAllUlchiWords(pageSize: Int, offset: Int): Flow<List<RussianWord>> {
        return ulchiDao.getAllWords(pageSize, offset).map { entities ->
            entities.map { entity ->
                entity.toDomain()
            }
        }
    }
    
    override fun searchRussianWords(query: String, pageSize: Int, offset: Int): Flow<List<RussianWord>> {
        return russianDao.searchWords(query, pageSize, offset).map { entities ->
            entities.map { entity ->
                entity.toDomain()
            }
        }
    }

    override fun searchUlchiWords(
        query: String,
        pageSize: Int,
        offset: Int
    ): Flow<List<RussianWord>> {
        return ulchiDao.searchWords(query, pageSize, offset).map { entities ->
            entities.map { entity ->
                entity.toDomain()
            }
        }
    }

    override suspend fun getSearchUlchiResultsCount(query: String): Int {
        return ulchiDao.getSearchResultsCount(query)
    }
    
    override suspend fun getRussianWordsCount(): Int {
        return russianDao.getWordsCount()
    }
    
    override suspend fun getSearchRussianResultsCount(query: String): Int {
        return russianDao.getSearchResultsCount(query)
    }

    override suspend fun getUlchiWordsCount(): Int {
        return ulchiDao.getWordsCount()
    }
} 