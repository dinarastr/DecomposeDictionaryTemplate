package ru.dinarastepina.ulchidictionary.data.repository

import ru.dinarastepina.ulchidictionary.data.local.dao.RussianDao
import ru.dinarastepina.ulchidictionary.data.local.dao.UlchiDao
import ru.dinarastepina.ulchidictionary.data.mapper.toDomain
import ru.dinarastepina.ulchidictionary.domain.model.Word
import ru.dinarastepina.ulchidictionary.domain.repository.DictionaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DictionaryRepositoryImpl(
    private val russianDao: RussianDao,
    private val ulchiDao: UlchiDao
) : DictionaryRepository {
    
    override fun getAllRussianWords(pageSize: Int, offset: Int): Flow<List<Word>> {
        return russianDao.getAllWords(pageSize, offset).map { entities ->
            entities.map { entity ->
                entity.toDomain()
            }
        }
    }

    override fun getAllUlchiWords(pageSize: Int, offset: Int): Flow<List<Word>> {
        return ulchiDao.getAllWords(pageSize, offset).map { entities ->
            entities.map { entity ->
                entity.toDomain()
            }
        }
    }
    
    override fun searchRussianWords(query: String, pageSize: Int, offset: Int): Flow<List<Word>> {
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
    ): Flow<List<Word>> {
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