package com.dinarastepina.decomposedictionary.data.repository

import com.dinarastepina.decomposedictionary.data.local.dao.PhraseBookDao
import com.dinarastepina.decomposedictionary.data.mapper.toDomain
import com.dinarastepina.decomposedictionary.domain.model.Phrase
import com.dinarastepina.decomposedictionary.domain.model.Topic
import com.dinarastepina.decomposedictionary.domain.repository.PhraseBookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PhraseBookRepositoryImpl(
    private val phraseBookDao: PhraseBookDao
): PhraseBookRepository {
    override suspend fun getTopics(): List<Topic> {
        return phraseBookDao.getAllTopics().map { it.toDomain() }
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