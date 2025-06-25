package ru.dinarastepina.ulchidictionary.data.paging

import ru.dinarastepina.ulchidictionary.domain.repository.DictionaryRepository
import ru.dinarastepina.ulchidictionary.domain.model.Word
import kotlinx.coroutines.flow.first

class UlchiWordPagingSource(
    private val repository: DictionaryRepository,
    searchQuery: String = ""
): BasePagingSource<Word>(searchQuery) {

    override suspend fun loadAllItems(pageSize: Int, offset: Int): List<Word> {
        return repository.getAllUlchiWords(pageSize, offset).first()
    }

    override suspend fun searchItems(query: String, pageSize: Int, offset: Int): List<Word> {
        return repository.searchUlchiWords(query, pageSize, offset).first()
    }
}