package com.dinarastepina.decomposedictionary.presentation.store

import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.dinarastepina.decomposedictionary.data.paging.RussianWordPagingSource
import com.dinarastepina.decomposedictionary.data.paging.UlchiWordPagingSource
import com.dinarastepina.decomposedictionary.data.repository.DictionaryRepository
import com.dinarastepina.decomposedictionary.domain.model.LANGUAGE
import com.dinarastepina.decomposedictionary.domain.model.RussianWord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * Store interface for the Dictionary component with paging support.
 */
interface DictionaryStore : Store<DictionaryStore.Intent, DictionaryStore.State, DictionaryStore.Label> {
    
    /**
     * Paging data flow for infinite scrolling.
     */
    val wordsPagingFlow: Flow<PagingData<RussianWord>>
    
    /**
     * Intents that can be sent to the store.
     */
    sealed class Intent {
        // Intent to search for a word
        data class Search(val query: String) : Intent()
        
        // Intent to clear the search
        data object ClearSearch : Intent()

        data object ChangeLanguage: Intent()

    }
    
    /**
     * State of the dictionary.
     */
    data class State(
        val query: String = "",
        val words: List<RussianWord> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val isInitialized: Boolean = false,
        val selectedLanguage: LANGUAGE = LANGUAGE.RUSSIAN,
        val targetLanguage: LANGUAGE = LANGUAGE.ULCHI
    )
    
    /**
     * One-time events emitted by the store.
     */
    sealed class Label {

        // Label for navigation events
        data class NavigateToWordDetail(val word: RussianWord) : Label()
    }
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
    @OptIn(ExperimentalCoroutinesApi::class)
    fun create(): DictionaryStore {
        // Create a scope for paging operations
        val pagingScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
        
        // Create the store
        val store = storeFactory.create(
            name = "DictionaryStore",
            initialState = DictionaryStore.State(),
            bootstrapper = BootstrapperImpl(),
            executorFactory = { ExecutorImpl() },
            reducer = ReducerImpl
        )
        
        return object : DictionaryStore, Store<DictionaryStore.Intent, DictionaryStore.State, DictionaryStore.Label> by store {
            // Paging implementation
            private val queryFlow = MutableStateFlow("")
            private val selectedLanguageFlow = MutableStateFlow(LANGUAGE.RUSSIAN)
            
            override val wordsPagingFlow: Flow<PagingData<RussianWord>> = combine(queryFlow, selectedLanguageFlow) { query, language ->
                query to language
            }.flatMapLatest { (query, language) ->
                when (language) {
                    LANGUAGE.RUSSIAN -> Pager(
                        config = PagingConfig(
                            pageSize = 20,
                            enablePlaceholders = false,
                            prefetchDistance = 5
                        ),
                        pagingSourceFactory = {
                            RussianWordPagingSource(
                                repository = repository,
                                searchQuery = query
                            )
                        }
                    ).flow

                    LANGUAGE.ULCHI -> Pager(
                        config = PagingConfig(
                            pageSize = 20,
                            enablePlaceholders = false,
                            prefetchDistance = 5
                        ),
                        pagingSourceFactory = {
                            UlchiWordPagingSource(
                                repository = repository,
                                searchQuery = query
                            )
                        }
                    ).flow
                }
            }

                init {
                    // Update query flow when state changes
                    store.stateFlow(pagingScope).onEach { storeState ->
                        queryFlow.value = storeState.query
                        selectedLanguageFlow.value = storeState.selectedLanguage
                    }.launchIn(pagingScope)
                }
            }
    }
    
    /**
     * Actions that can occur within the store - these are internal events
     * triggered by the bootstrapper or executor for complex business logic.
     */
    private sealed class Action {
        // Action to initialize the store
        data object Initialize : Action()

        // Action to refresh data periodically
        data object RefreshData : Action()
        
        // Action to handle search debouncing
        data class PerformDebouncedSearch(val query: String) : Action()
    }
    
    /**
     * Messages that can be sent to the reducer.
     */
    private sealed class Message {
        data class WordsLoaded(val words: List<RussianWord>) : Message()
        data class QueryChanged(val query: String) : Message()
        data class ErrorOccurred(val error: String) : Message()

        data object LoadingStarted : Message()
        data object SearchCleared : Message()
        data object Initialized : Message()
        data object LanguageChanged: Message()
    }
    
    /**
     * Bootstrapper that sends initial actions when the store is created.
     */
    private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            // Send initial actions when store is created
            dispatch(Action.Initialize)
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
                is DictionaryStore.Intent.ChangeLanguage -> changelanguage()
            }
        }
        
        override fun executeAction(action: Action) {
            when (action) {
                is Action.Initialize -> initialize()
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
        
        private fun selectWord(word: RussianWord) {
            // Publish label for navigation
            publish(DictionaryStore.Label.NavigateToWordDetail(word))
        }
        
        private fun clearSearch() {
            dispatch(Message.SearchCleared)
        }

        private fun changelanguage() {
            dispatch(Message.LanguageChanged)
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
        
        private fun refreshData() {
            scope.launch {
                try {
                    // Refresh both current search and popular words
                    val currentQuery = state().query
                    if (currentQuery.isNotBlank()) {
                        repository.searchRussianWords(currentQuery, pageSize = 20, offset = 0)
                            .onEach { domainWords ->
                                dispatch(Message.WordsLoaded(domainWords))
                            }
                            .launchIn(scope)
                    }
                    // Refresh popular words
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
                        repository.searchRussianWords(query, pageSize = 20, offset = 0)
                            .onEach { domainWords ->
                                dispatch(Message.WordsLoaded(domainWords))
                            }
                            .launchIn(scope)
                    }
                } catch (e: Exception) {
                    dispatch(Message.ErrorOccurred(e.message ?: "Search failed"))
                }
            }
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
                is Message.LanguageChanged -> copy(
                    selectedLanguage = if (selectedLanguage == LANGUAGE.RUSSIAN) LANGUAGE.ULCHI else LANGUAGE.RUSSIAN
                )
            }
    }
}