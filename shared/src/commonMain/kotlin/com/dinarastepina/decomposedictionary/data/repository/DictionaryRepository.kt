package com.dinarastepina.decomposedictionary.data.repository

import com.dinarastepina.decomposedictionary.domain.model.RussianWord
import kotlinx.coroutines.flow.Flow

interface DictionaryRepository {

    fun getAllRussianWords(pageSize: Int, offset: Int): Flow<List<RussianWord>>

    fun searchRussianWords(query: String, pageSize: Int, offset: Int): Flow<List<RussianWord>>

    suspend fun getRussianWordsCount(): Int

    suspend fun getSearchRussianResultsCount(query: String): Int

    fun getAllUlchiWords(pageSize: Int, offset: Int): Flow<List<RussianWord>>

    fun searchUlchiWords(query: String, pageSize: Int, offset: Int): Flow<List<RussianWord>>

    suspend fun getUlchiWordsCount(): Int

    suspend fun getSearchUlchiResultsCount(query: String): Int
} 