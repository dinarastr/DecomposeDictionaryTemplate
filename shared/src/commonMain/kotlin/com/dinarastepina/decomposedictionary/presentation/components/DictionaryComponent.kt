package com.dinarastepina.decomposedictionary.presentation.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.operator.map
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.mapNotNull
import com.dinarastepina.decomposedictionary.presentation.store.DictionaryStore
import com.dinarastepina.decomposedictionary.presentation.store.DictionaryStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

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
    
    // Method to select a word
    fun selectWord(word: DictionaryStore.Word)
    
    // Method to load popular words
    fun loadPopularWords()

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
}

/**
 * Default implementation of the DictionaryComponent interface.
 */
class DefaultDictionaryComponent(
    componentContext: ComponentContext,
    private val storeFactory: StoreFactory = DefaultStoreFactory(),
) : DictionaryComponent, ComponentContext by componentContext {

    // Create the store
    private val store = DictionaryStoreFactory(storeFactory).create()

    // Map the store state to the component state
    override val state: Value<DictionaryComponent.State> = store.asValue().map { storeState ->
        DictionaryComponent.State(
            query = storeState.query,
            words = storeState.words,
            popularWords = storeState.popularWords,
            isLoading = storeState.isLoading,
            error = storeState.error,
            isInitialized = storeState.isInitialized
        )
    }

    // Map the store labels to word selections
    override val wordSelections: Flow<DictionaryStore.Word> = store.labels.mapNotNull { label ->
        when (label) {
            is DictionaryStore.Label.WordSelected -> label.word
            else -> null
        }
    }
    
    // Map the store labels to navigation events
    override val navigationEvents: Flow<DictionaryStore.Word> = store.labels.mapNotNull { label ->
        when (label) {
            is DictionaryStore.Label.NavigateToWordDetail -> label.word
            else -> null
        }
    }

    // Method to search for a word
    override fun search(query: String) {
        store.accept(DictionaryStore.Intent.Search(query))
    }

    // Method to clear the search
    override fun clearSearch() {
        store.accept(DictionaryStore.Intent.ClearSearch)
    }
    
    // Method to select a word
    override fun selectWord(word: DictionaryStore.Word) {
        store.accept(DictionaryStore.Intent.SelectWord(word))
    }
    
    // Method to load popular words
    override fun loadPopularWords() {
        store.accept(DictionaryStore.Intent.LoadPopularWords)
    }

    // Helper extension to convert a Store to a Value
    private fun <T : Any> com.arkivanov.mvikotlin.core.store.Store<*, T, *>.asValue(): Value<T> {
        val scope = CoroutineScope(Dispatchers.Main)
        val stateFlow = stateFlow(scope)
        return object : Value<T>() {
            override val value: T get() = stateFlow.value

            override fun subscribe(observer: (T) -> Unit): com.arkivanov.decompose.Cancellation {
                val job = stateFlow
                    .onEach(observer)
                    .launchIn(scope)

                return com.arkivanov.decompose.Cancellation {
                    job.cancel()
                    scope.cancel()
                }
            }
        }
    }
}