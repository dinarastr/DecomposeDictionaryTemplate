package com.dinarastepina.decomposedictionary.domain.repository

import com.dinarastepina.decomposedictionary.domain.model.Word
import kotlinx.coroutines.flow.Flow

interface DictionaryRepository {

    fun getAllRussianWords(pageSize: Int, offset: Int): Flow<List<Word>>

    fun searchRussianWords(query: String, pageSize: Int, offset: Int): Flow<List<Word>>

    suspend fun getRussianWordsCount(): Int

    suspend fun getSearchRussianResultsCount(query: String): Int

    fun getAllUlchiWords(pageSize: Int, offset: Int): Flow<List<Word>>

    fun searchUlchiWords(query: String, pageSize: Int, offset: Int): Flow<List<Word>>

    suspend fun getUlchiWordsCount(): Int

    suspend fun getSearchUlchiResultsCount(query: String): Int
}