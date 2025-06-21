package com.dinarastepina.decomposedictionary.data.repository

import com.dinarastepina.decomposedictionary.domain.model.Word
import kotlinx.coroutines.flow.Flow

interface DictionaryRepository {

    fun getAllWords(): Flow<List<Word>>

    fun searchWords(query: String): Flow<List<Word>>
} 