package ru.dinarastepina.ulchidictionary.presentation.store

import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import app.cash.paging.Pager
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import ru.dinarastepina.ulchidictionary.data.paging.RussianWordPagingSource
import ru.dinarastepina.ulchidictionary.data.paging.UlchiWordPagingSource
import ru.dinarastepina.ulchidictionary.domain.repository.DataStoreRepository
import ru.dinarastepina.ulchidictionary.domain.repository.DictionaryRepository
import ru.dinarastepina.ulchidictionary.domain.model.LANGUAGE
import ru.dinarastepina.ulchidictionary.domain.model.Word
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

interface DictionaryStore :
    Store<DictionaryStore.Intent, DictionaryStore.State, DictionaryStore.Label> {

    val wordsPagingFlow: Flow<PagingData<Word>>

    sealed class Intent {
        data class Search(val query: String) : Intent()

        data object ClearSearch : Intent()

        data class UpdateSelectedLanguage(val language: LANGUAGE) : Intent()
    }

    data class State(
        val query: String = "",
        val error: String? = null,
        val isInitialized: Boolean = false,
        val selectedLanguage: LANGUAGE = LANGUAGE.RUSSIAN,
        val targetLanguage: LANGUAGE = LANGUAGE.ULCHI
    )

    sealed class Label {
        data class NavigateToWordDetail(val word: Word) : Label()
    }
}

class DictionaryStoreFactory(
    private val storeFactory: StoreFactory,
    private val dictionaryRepository: DictionaryRepository,
    private val dataStoreRepository: DataStoreRepository
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    fun create(): DictionaryStore {
        val pagingScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

        val store = storeFactory.create(
            name = "DictionaryStore",
            initialState = DictionaryStore.State(),
            bootstrapper = BootstrapperImpl(),
            executorFactory = { ExecutorImpl() },
            reducer = ReducerImpl
        )

        return object : DictionaryStore,
            Store<DictionaryStore.Intent, DictionaryStore.State, DictionaryStore.Label> by store {
            private val queryFlow = MutableStateFlow("")
            private val selectedLanguageFlow = MutableStateFlow(LANGUAGE.RUSSIAN)

            override val wordsPagingFlow: Flow<PagingData<Word>> =
                combine(queryFlow, selectedLanguageFlow) { query, language ->
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
                                    repository = dictionaryRepository,
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
                                    repository = dictionaryRepository,
                                    searchQuery = query
                                )
                            }
                        ).flow
                    }
                }

            init {
                store.stateFlow(pagingScope).onEach { storeState ->
                    queryFlow.value = storeState.query
                    selectedLanguageFlow.value = storeState.selectedLanguage
                }.launchIn(pagingScope)
            }
        }
    }

    private sealed class Action {
        data object Initialize : Action()
        
        data object SubscribeToLanguageUpdates : Action()
    }

    private sealed class Message {
        data class QueryChanged(val query: String) : Message()
        data class ErrorOccurred(val error: String) : Message()
        data object SearchCleared : Message()
        data object Initialized : Message()
        data class SelectedLanguageUpdated(val language: LANGUAGE) : Message()
    }

    private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            dispatch(Action.Initialize)
            dispatch(Action.SubscribeToLanguageUpdates)
        }
    }

    private inner class ExecutorImpl :
        CoroutineExecutor<DictionaryStore.Intent, Action, DictionaryStore.State, Message, DictionaryStore.Label>() {

        override fun executeIntent(intent: DictionaryStore.Intent) {
            when (intent) {
                is DictionaryStore.Intent.Search -> handleSearch(intent.query)
                is DictionaryStore.Intent.ClearSearch -> clearSearch()
                is DictionaryStore.Intent.UpdateSelectedLanguage -> updateSelectedLanguage(intent.language)
            }
        }

        override fun executeAction(action: Action) {
            when (action) {
                is Action.Initialize -> initialize()
                is Action.SubscribeToLanguageUpdates -> subscribeToLanguageUpdates()
            }
        }

        private fun handleSearch(query: String) {
            dispatch(Message.QueryChanged(query))

            if (query.isBlank()) {
                dispatch(Message.SearchCleared)
            }
        }

        private fun clearSearch() {
            dispatch(Message.SearchCleared)
        }

        private fun updateSelectedLanguage(language: LANGUAGE) {
            dispatch(Message.SelectedLanguageUpdated(language))
            
            scope.launch {
                try {
                    dataStoreRepository.setLastSelectedLanguage(language.value)
                } catch (e: Exception) {
                    dispatch(Message.ErrorOccurred("Failed to save language preference: ${e.message}"))
                }
            }
        }

        private fun subscribeToLanguageUpdates() {
            scope.launch {
                try {
                    dataStoreRepository.lastSelectedLanguage.collect { languageString ->
                        val language = LANGUAGE.fromString(languageString)
                        dispatch(Message.SelectedLanguageUpdated(language))
                    }
                } catch (e: Exception) {
                    dispatch(Message.ErrorOccurred("Failed to load language preference: ${e.message}"))
                }
            }
        }

        private fun initialize() {
            scope.launch {
                try {
                    dispatch(Message.Initialized)
                } catch (e: Exception) {
                    dispatch(Message.ErrorOccurred("Failed to initialize: ${e.message}"))
                }
            }
        }


    }

    private object ReducerImpl : Reducer<DictionaryStore.State, Message> {
        override fun DictionaryStore.State.reduce(msg: Message): DictionaryStore.State =
            when (msg) {
                is Message.QueryChanged -> copy(
                    query = msg.query
                )

                is Message.ErrorOccurred -> copy(
                    error = msg.error
                )

                is Message.SearchCleared -> copy(
                    query = "",
                    error = null
                )

                is Message.Initialized -> copy(
                    isInitialized = true
                )

                is Message.SelectedLanguageUpdated -> copy(
                    selectedLanguage = msg.language,
                    targetLanguage = LANGUAGE.targetLanguage(msg.language)
                )
            }
    }
}