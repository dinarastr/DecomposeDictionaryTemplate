package com.dinarastepina.decomposedictionary.presentation.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.dinarastepina.decomposedictionary.data.repository.DictionaryRepository
import com.dinarastepina.decomposedictionary.domain.model.Word
import com.dinarastepina.decomposedictionary.domain.model.Word as DomainWord
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * Store interface for the Dictionary component.
 */
interface DictionaryStore : Store<DictionaryStore.Intent, DictionaryStore.State, DictionaryStore.Label> {
    
    /**
     * Intents that can be sent to the store.
     */
    sealed class Intent {
        // Intent to search for a word
        data class Search(val query: String) : Intent()
        
        // Intent to clear the search
        data object ClearSearch : Intent()
        
        // Intent to select a word
        data class SelectWord(val word: Word) : Intent()
        
        // Intent to load popular words
        data object LoadPopularWords : Intent()
    }
    
    /**
     * State of the dictionary.
     */
    data class State(
        val query: String = "",
        val words: List<Word> = emptyList(),
        val popularWords: List<Word> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val isInitialized: Boolean = false
    )
    
    /**
     * One-time events emitted by the store.
     */
    sealed class Label {
        // Label for when a word is selected
        data class WordSelected(val word: Word) : Label()
        
        // Label for navigation events
        data class NavigateToWordDetail(val word: Word) : Label()
    }
    
    /**
     * Data class representing a word in the dictionary.
     */
    data class Word(
        val id: String,
        val text: String,
        val definition: String,
        val examples: List<String> = emptyList()
    )
}

/**
 * Factory for creating the DictionaryStore.
 */
class DictionaryStoreFactory(
    private val storeFactory: StoreFactory,
    private val repository: DictionaryRepository
) {
    
    /**
     * Creates a new instance of the DictionaryStore.
     */
    fun create(): DictionaryStore =
        object : DictionaryStore, Store<DictionaryStore.Intent, DictionaryStore.State, DictionaryStore.Label> by storeFactory.create(
            name = "DictionaryStore",
            initialState = DictionaryStore.State(),
            bootstrapper = BootstrapperImpl(),
            executorFactory = { ExecutorImpl() },
            reducer = ReducerImpl
        ) {}
    
    /**
     * Actions that can occur within the store - these are internal events
     * triggered by the bootstrapper or executor for complex business logic.
     */
    private sealed class Action {
        // Action to initialize the store
        data object Initialize : Action()
        
        // Action to load popular words automatically
        data object LoadPopularWords : Action()
        
        // Action to refresh data periodically
        data object RefreshData : Action()
        
        // Action to handle search debouncing
        data class PerformDebouncedSearch(val query: String) : Action()
    }
    
    /**
     * Messages that can be sent to the reducer.
     */
    private sealed class Message {
        data class WordsLoaded(val words: List<DictionaryStore.Word>) : Message()
        data class PopularWordsLoaded(val words: List<DictionaryStore.Word>) : Message()
        data class QueryChanged(val query: String) : Message()
        data class ErrorOccurred(val error: String) : Message()
        data object LoadingStarted : Message()
        data object SearchCleared : Message()
        data object Initialized : Message()
    }
    
    /**
     * Bootstrapper that sends initial actions when the store is created.
     */
    private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            // Send initial actions when store is created
            dispatch(Action.Initialize)
            dispatch(Action.LoadPopularWords)
        }
    }
    
    /**
     * Executor that handles business logic.
     */
    private inner class ExecutorImpl : CoroutineExecutor<DictionaryStore.Intent, Action, DictionaryStore.State, Message, DictionaryStore.Label>() {
        
        override fun executeIntent(intent: DictionaryStore.Intent) {
            when (intent) {
                is DictionaryStore.Intent.Search -> handleSearch(intent.query)
                is DictionaryStore.Intent.ClearSearch -> clearSearch()
                is DictionaryStore.Intent.SelectWord -> selectWord(intent.word)
                is DictionaryStore.Intent.LoadPopularWords -> {
                    // Intent can trigger an action for complex logic
                    executeAction(Action.LoadPopularWords)
                }
            }
        }
        
        override fun executeAction(action: Action) {
            when (action) {
                is Action.Initialize -> initialize()
                is Action.LoadPopularWords -> loadPopularWords()
                is Action.RefreshData -> refreshData()
                is Action.PerformDebouncedSearch -> performDebouncedSearch(action.query)
            }
        }
        
        private fun handleSearch(query: String) {
            dispatch(Message.QueryChanged(query))
            
            if (query.isBlank()) {
                dispatch(Message.SearchCleared)
                return
            }
            
            // Use action for debounced search logic
            executeAction(Action.PerformDebouncedSearch(query))
        }
        
        private fun selectWord(word: DictionaryStore.Word) {
            // Publish label for navigation
            publish(DictionaryStore.Label.WordSelected(word))
            publish(DictionaryStore.Label.NavigateToWordDetail(word))
        }
        
        private fun clearSearch() {
            dispatch(Message.SearchCleared)
        }
        
        private fun initialize() {
            scope.launch {
                try {
                    // Simulate initialization logic
                    kotlinx.coroutines.delay(500)
                    dispatch(Message.Initialized)
                } catch (e: Exception) {
                    dispatch(Message.ErrorOccurred("Failed to initialize: ${e.message}"))
                }
            }
        }
        
        private fun loadPopularWords() {
            scope.launch {
                try {
                    dispatch(Message.LoadingStarted)
                    val domainWords = emptyList<Word>()
                        //repository.getPopularWords()
                    val storeWords = domainWords.map { it.toStoreWord() }
                    dispatch(Message.PopularWordsLoaded(storeWords))
                } catch (e: Exception) {
                    dispatch(Message.ErrorOccurred("Failed to load popular words: ${e.message}"))
                }
            }
        }
        
        private fun refreshData() {
            scope.launch {
                try {
                    // Refresh both current search and popular words
                    val currentQuery = state().query
                    if (currentQuery.isNotBlank()) {
                        //val words = fetchWords(currentQuery)
                        dispatch(Message.WordsLoaded(emptyList()))
                    }
                    //val popularWords = fetchPopularWords()
                    dispatch(Message.PopularWordsLoaded(emptyList()))
                } catch (e: Exception) {
                    dispatch(Message.ErrorOccurred("Failed to refresh data: ${e.message}"))
                }
            }
        }
        
        private fun performDebouncedSearch(query: String) {
            scope.launch {
                try {
                    dispatch(Message.LoadingStarted)
                    // Simulate debouncing delay
                    kotlinx.coroutines.delay(300)
                    
                    // Check if query is still current
                    if (state().query == query) {
                        repository.searchWords(query)
                            .onEach { domainWords ->
                                val storeWords = domainWords.map { it.toStoreWord() }
                                dispatch(Message.WordsLoaded(storeWords))
                            }
                            .launchIn(scope)
                    }
                } catch (e: Exception) {
                    dispatch(Message.ErrorOccurred(e.message ?: "Search failed"))
                }
            }
        }
        
        // Extension function to convert domain word to store word
        private fun DomainWord.toStoreWord(): DictionaryStore.Word {
            return DictionaryStore.Word(
                id = this.id,
                text = this.text,
                definition = this.definition,
                examples = this.examples
            )
        }
    }
    
    /**
     * Reducer that updates the state based on messages.
     */
    private object ReducerImpl : Reducer<DictionaryStore.State, Message> {
        override fun DictionaryStore.State.reduce(message: Message): DictionaryStore.State =
            when (message) {
                is Message.WordsLoaded -> copy(
                    words = message.words,
                    isLoading = false,
                    error = null
                )
                is Message.PopularWordsLoaded -> copy(
                    popularWords = message.words,
                    isLoading = false,
                    error = null
                )
                is Message.QueryChanged -> copy(
                    query = message.query
                )
                is Message.ErrorOccurred -> copy(
                    error = message.error,
                    isLoading = false
                )
                is Message.LoadingStarted -> copy(
                    isLoading = true,
                    error = null
                )
                is Message.SearchCleared -> copy(
                    query = "",
                    words = emptyList(),
                    isLoading = false,
                    error = null
                )
                is Message.Initialized -> copy(
                    isInitialized = true
                )
            }
    }
}