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
import com.dinarastepina.decomposedictionary.audio.AudioTrack
import com.dinarastepina.decomposedictionary.audio.PlaylistManager
import com.dinarastepina.decomposedictionary.data.paging.PhraseBookPagingSource
import com.dinarastepina.decomposedictionary.domain.model.Phrase
import com.dinarastepina.decomposedictionary.domain.repository.PhraseBookRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

interface SearchStore : Store<SearchStore.Intent, SearchStore.State, SearchStore.Label> {

    val phrasesPagingFlow: Flow<PagingData<Phrase>>

    sealed class Intent {
        data class Search(val query: String) : Intent()
        data object ClearSearch : Intent()
        data class PlayAudio(val phrase: Phrase) : Intent()
        data object PauseAudio : Intent()
        data object StopAudio : Intent()
    }

    data class State(
        val query: String = "",
        val currentlyPlayingPhrase: Phrase? = null,
        val isPlaying: Boolean = false,
        val error: String? = null
    )

    sealed class Label {
        data object NavigateBack : Label()
    }
}

class SearchStoreFactory(
    private val storeFactory: StoreFactory,
    private val phraseBookRepository: PhraseBookRepository,
    private val playlistManager: PlaylistManager
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun create(): SearchStore {
        val pagingScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

        val store = storeFactory.create(
            name = "SearchStore",
            initialState = SearchStore.State(),
            bootstrapper = BootstrapperImpl(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        )

        return object : SearchStore,
            Store<SearchStore.Intent, SearchStore.State, SearchStore.Label> by store {
            private val queryFlow = MutableStateFlow("")

            override val phrasesPagingFlow: Flow<PagingData<Phrase>> =
                queryFlow.flatMapLatest { query ->
                    Pager(
                        config = PagingConfig(
                            pageSize = 20,
                            enablePlaceholders = false,
                            prefetchDistance = 5
                        ),
                        pagingSourceFactory = {
                            PhraseBookPagingSource(
                                repository = phraseBookRepository,
                                searchQuery = query
                            )
                        }
                    ).flow
                }

            init {
                store.stateFlow(pagingScope).onEach { storeState ->
                    queryFlow.value = storeState.query
                }.launchIn(pagingScope)
            }
        }
    }

    private sealed class Action {
        data object Initialize : Action()
    }

    private sealed class Message {
        data class QueryUpdated(val query: String) : Message()
        data class ErrorOccurred(val error: String) : Message()
        
        data class AudioStarted(val phrase: Phrase) : Message()
        data object AudioPaused : Message()
        data object AudioStopped : Message()
        data object AudioCompleted : Message()
    }

    private class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            dispatch(Action.Initialize)
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<SearchStore.Intent, Action, SearchStore.State, Message, SearchStore.Label>() {
        
        init {
            playlistManager.setOnTrackCompletedListener {
                dispatch(Message.AudioCompleted)
            }
        }

        override fun executeIntent(intent: SearchStore.Intent) {
            when (intent) {
                is SearchStore.Intent.Search -> search(intent.query)
                is SearchStore.Intent.ClearSearch -> clearSearch()
                is SearchStore.Intent.PlayAudio -> playAudio(intent.phrase)
                is SearchStore.Intent.PauseAudio -> pauseAudio()
                is SearchStore.Intent.StopAudio -> stopAudio()
            }
        }

        override fun executeAction(action: Action) {
            when (action) {
                is Action.Initialize -> {
                    // Initialize with empty query to show all phrases
                    dispatch(Message.QueryUpdated(""))
                }
            }
        }

        private fun search(query: String) {
            dispatch(Message.QueryUpdated(query))
        }

        private fun clearSearch() {
            dispatch(Message.QueryUpdated(""))
        }

        // Audio methods - reusing logic pattern from PhrasesStore
        private fun playAudio(phrase: Phrase) {
            scope.launch {
                try {
                    val currentState = state()
                    if (currentState.currentlyPlayingPhrase?.id == phrase.id && currentState.isPlaying) {
                        pauseAudio()
                        return@launch
                    }
                    
                    if (currentState.currentlyPlayingPhrase?.id == phrase.id && !currentState.isPlaying) {
                        playlistManager.playCurrentTrack()
                        dispatch(Message.AudioStarted(phrase))
                        return@launch
                    }
                    
                    val audioTrack = AudioTrack(
                        id = phrase.id,
                        path = "${phrase.audio}.mp3",
                        title = phrase.ulchi,
                        artist = "Ulchi Dictionary"
                    )
                    
                    playlistManager.playTrack(audioTrack)
                    dispatch(Message.AudioStarted(phrase))
                } catch (e: Exception) {
                    dispatch(Message.ErrorOccurred("Failed to play audio: ${e.message}"))
                }
            }
        }

        private fun pauseAudio() {
            playlistManager.pause()
            dispatch(Message.AudioPaused)
        }

        private fun stopAudio() {
            playlistManager.stop()
            dispatch(Message.AudioStopped)
        }
    }

    private object ReducerImpl : Reducer<SearchStore.State, Message> {
        override fun SearchStore.State.reduce(message: Message): SearchStore.State =
            when (message) {
                is Message.QueryUpdated -> copy(
                    query = message.query,
                    error = null
                )
                is Message.ErrorOccurred -> copy(
                    error = message.error
                )
                is Message.AudioStarted -> copy(
                    currentlyPlayingPhrase = message.phrase,
                    isPlaying = true,
                    error = null
                )
                is Message.AudioPaused -> copy(
                    isPlaying = false
                )
                is Message.AudioStopped -> copy(
                    currentlyPlayingPhrase = null,
                    isPlaying = false
                )
                is Message.AudioCompleted -> copy(
                    currentlyPlayingPhrase = null,
                    isPlaying = false
                )
            }
    }
} 