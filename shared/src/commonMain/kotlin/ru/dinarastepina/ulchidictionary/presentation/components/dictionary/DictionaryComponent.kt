package ru.dinarastepina.ulchidictionary.presentation.components.dictionary

import app.cash.paging.PagingData
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import ru.dinarastepina.ulchidictionary.domain.model.LANGUAGE
import ru.dinarastepina.ulchidictionary.domain.model.Word
import kotlinx.coroutines.flow.Flow

interface DictionaryComponent {
    val state: Value<State>

    val navigationEvents: Flow<Word>

    val wordsPagingFlow: Flow<PagingData<Word>>

    fun search(query: String)

    fun clearSearch()

    fun changeLanguage(language: LANGUAGE)

    data class State(
        val query: String = "",
        val selectedLanguage: LANGUAGE = LANGUAGE.RUSSIAN,
        val targetLanguage: LANGUAGE = LANGUAGE.ULCHI,
        val error: String? = null,
        val isInitialized: Boolean = false
    )
    
    fun interface Factory {
        operator fun invoke(componentContext: ComponentContext): DictionaryComponent
    }
}