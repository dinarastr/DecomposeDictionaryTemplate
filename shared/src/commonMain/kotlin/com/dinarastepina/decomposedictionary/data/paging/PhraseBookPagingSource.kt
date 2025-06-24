package com.dinarastepina.decomposedictionary.data.paging

import com.dinarastepina.decomposedictionary.domain.model.Phrase
import com.dinarastepina.decomposedictionary.domain.repository.PhraseBookRepository
import kotlinx.coroutines.flow.first

class PhraseBookPagingSource(
    private val repository: PhraseBookRepository,
    searchQuery: String = ""
): BasePagingSource<Phrase>(searchQuery) {
    override suspend fun loadAllItems(pageSize: Int, offset: Int): List<Phrase> {
        return repository.getAllPhrases(pageSize = pageSize, offset = offset).first()
    }

    override suspend fun searchItems(query: String, pageSize: Int, offset: Int): List<Phrase> {
        return repository.searchPhrases(query, pageSize = pageSize, offset = offset).first()
    }
}