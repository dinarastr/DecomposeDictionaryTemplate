package com.dinarastepina.decomposedictionary.data.paging

import com.dinarastepina.decomposedictionary.data.repository.DictionaryRepository
import com.dinarastepina.decomposedictionary.domain.model.Word
import kotlinx.coroutines.flow.first

class RussianWordPagingSource(
    private val repository: DictionaryRepository,
    searchQuery: String = ""
) : BasePagingSource<Word>(searchQuery) {

    override suspend fun loadAllItems(pageSize: Int, offset: Int): List<Word> {
        return repository.getAllRussianWords(pageSize, offset).first()
    }

    override suspend fun searchItems(query: String, pageSize: Int, offset: Int): List<Word> {
        return repository.searchRussianWords(query, pageSize, offset).first()
    }
} 