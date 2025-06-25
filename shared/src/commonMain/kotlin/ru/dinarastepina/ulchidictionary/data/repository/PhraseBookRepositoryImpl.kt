package ru.dinarastepina.ulchidictionary.data.repository

import ru.dinarastepina.ulchidictionary.data.local.dao.PhraseBookDao
import ru.dinarastepina.ulchidictionary.data.mapper.toDomain
import ru.dinarastepina.ulchidictionary.domain.model.Phrase
import ru.dinarastepina.ulchidictionary.domain.model.Topic
import ru.dinarastepina.ulchidictionary.domain.repository.PhraseBookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class PhraseBookRepositoryImpl(
    private val phraseBookDao: PhraseBookDao
): PhraseBookRepository {
    
    // Memory cache for topics to avoid repeated database queries
    private var cachedTopics: List<Topic>? = null
    private val topicsCacheMutex = Mutex()
    
    override suspend fun getTopics(): List<Topic> {
        return topicsCacheMutex.withLock {
            cachedTopics?.let { topics ->
                // Return cached topics if available
                topics
            } ?: run {
                // Load from database and cache
                val topicsFromDb = phraseBookDao.getAllTopics().map { it.toDomain() }
                cachedTopics = topicsFromDb
                topicsFromDb
            }
        }
    }

    override suspend fun getPhrasesByTopic(topicId: Int): List<Phrase> {
        return phraseBookDao.getPhrasesByTopic(topicId).map { it.toDomain() }
    }

    override fun getAllPhrases(offset: Int, pageSize: Int): Flow<List<Phrase>> {
        return phraseBookDao.getAllPhrases(pageSize = pageSize, offset = offset).map { phrases ->
            phrases.map { it.toDomain() }
        }
    }

    override fun searchPhrases(
        query: String,
        offset: Int,
        pageSize: Int
    ): Flow<List<Phrase>> {
        return phraseBookDao.searchPhrases(query, pageSize = pageSize, offset = offset).map { phrases ->
            phrases.map { it.toDomain() }
        }
    }

    override suspend fun getPhrasesCount(): Int {
        return phraseBookDao.getPhrasesCount()
    }

    override suspend fun getSearchResultsCount(query: String): Int {
        return phraseBookDao.getSearchResultsCount(query)
    }
}