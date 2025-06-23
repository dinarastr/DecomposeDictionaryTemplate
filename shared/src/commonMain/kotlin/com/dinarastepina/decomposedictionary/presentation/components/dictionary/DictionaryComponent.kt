package com.dinarastepina.decomposedictionary.presentation.components.dictionary

import app.cash.paging.PagingData
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.dinarastepina.decomposedictionary.domain.model.LANGUAGE
import com.dinarastepina.decomposedictionary.domain.model.RussianWord
import kotlinx.coroutines.flow.Flow

/**
 * Dictionary component that displays the dictionary functionality.
 */
interface DictionaryComponent {
    // Expose the state as a Value (observable)
    val state: Value<State>

    // Expose navigation events as a Flow
    val navigationEvents: Flow<RussianWord>

    // Expose paging flow for infinite scrolling
    val wordsPagingFlow: Flow<PagingData<RussianWord>>

    // Method to search for a word
    fun search(query: String)

    // Method to clear the search
    fun clearSearch()

    fun changeLanguage(language: LANGUAGE)

    /**
     * State of the dictionary component.
     */
    data class State(
        val query: String = "",
        val selectedLanguage: LANGUAGE = LANGUAGE.RUSSIAN,
        val targetLanguage: LANGUAGE = LANGUAGE.ULCHI,
        val words: List<RussianWord> = emptyList(),
        val popularWords: List<RussianWord> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val isInitialized: Boolean = false
    )
    
    // Factory interface for creating DictionaryComponent instances
    fun interface Factory {
        operator fun invoke(componentContext: ComponentContext): DictionaryComponent
    }
}