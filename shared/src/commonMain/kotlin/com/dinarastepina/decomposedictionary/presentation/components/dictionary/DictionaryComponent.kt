package com.dinarastepina.decomposedictionary.presentation.components.dictionary

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.dinarastepina.decomposedictionary.presentation.store.DictionaryStore
import kotlinx.coroutines.flow.Flow

/**
 * Dictionary component that displays the dictionary functionality.
 */
interface DictionaryComponent {
    // Expose the state as a Value (observable)
    val state: Value<State>

    // Expose word selection events as a Flow
    val wordSelections: Flow<DictionaryStore.Word>
    
    // Expose navigation events as a Flow
    val navigationEvents: Flow<DictionaryStore.Word>

    // Method to search for a word
    fun search(query: String)

    // Method to clear the search
    fun clearSearch()

    /**
     * State of the dictionary component.
     */
    data class State(
        val query: String = "",
        val words: List<DictionaryStore.Word> = emptyList(),
        val popularWords: List<DictionaryStore.Word> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val isInitialized: Boolean = false
    )
    
    // Factory interface for creating DictionaryComponent instances
    fun interface Factory {
        operator fun invoke(componentContext: ComponentContext): DictionaryComponent
    }
}