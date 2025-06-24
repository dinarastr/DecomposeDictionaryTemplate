package com.dinarastepina.decomposedictionary.domain.repository

import com.dinarastepina.decomposedictionary.domain.model.Phrase
import com.dinarastepina.decomposedictionary.domain.model.Topic
import kotlinx.coroutines.flow.Flow

interface PhraseBookRepository {
    suspend fun getTopics(): List<Topic>
    suspend fun getPhrasesByTopic(topicId: Int): List<Phrase>
    fun searchPhrases(query: String, offset: Int, pageSize: Int): Flow<List<Phrase>>
    fun getAllPhrases(offset: Int, pageSize: Int): Flow<List<Phrase>>
    suspend fun getSearchResultsCount(query: String): Int
    suspend fun getPhrasesCount(): Int
}