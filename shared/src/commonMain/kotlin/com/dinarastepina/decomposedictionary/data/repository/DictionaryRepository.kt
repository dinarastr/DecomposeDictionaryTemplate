package com.dinarastepina.decomposedictionary.data.repository

import com.dinarastepina.decomposedictionary.domain.model.Word
import kotlinx.coroutines.flow.Flow

interface DictionaryRepository {

    fun getAllWords(pageSize: Int, offset: Int): Flow<List<Word>>

    fun searchWords(query: String, pageSize: Int, offset: Int): Flow<List<Word>>

    suspend fun getWordsCount(): Int

    suspend fun getSearchResultsCount(query: String): Int
} 