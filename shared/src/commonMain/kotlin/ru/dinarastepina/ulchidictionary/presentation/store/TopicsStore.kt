package ru.dinarastepina.ulchidictionary.presentation.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import ru.dinarastepina.ulchidictionary.domain.model.Topic
import ru.dinarastepina.ulchidictionary.domain.repository.PhraseBookRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface TopicsStore : Store<TopicsStore.Intent, TopicsStore.State, TopicsStore.Label> {

    sealed class Intent {
        data object LoadTopics : Intent()
        data class SelectTopic(val topic: Topic) : Intent()
    }

    data class State(
        val topics: List<Topic> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    )

    sealed class Label {
        data class NavigateToPhrases(val topic: Topic) : Label()
    }
}

class TopicsStoreFactory(
    private val storeFactory: StoreFactory,
    private val phraseBookRepository: PhraseBookRepository
) {
    
    // Cache topics to avoid repeated database queries
    private var cachedTopics: List<Topic>? = null
    
    fun create(): TopicsStore =
        object : TopicsStore, Store<TopicsStore.Intent, TopicsStore.State, TopicsStore.Label> by storeFactory.create(
            name = "TopicsStore",
            initialState = TopicsStore.State(isLoading = true),
            bootstrapper = BootstrapperImpl(),
            executorFactory = { ExecutorImpl() },
            reducer = ReducerImpl
        ) {}

    private sealed class Action {
        data object LoadTopics : Action()
    }

    private sealed class Message {
        data object LoadingStarted : Message()
        data class TopicsLoaded(val topics: List<Topic>) : Message()
        data class ErrorOccurred(val error: String) : Message()
    }

    private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            // Load immediately if cached, otherwise dispatch action
            cachedTopics?.let { topics ->
                dispatch(Action.LoadTopics)
            } ?: run {
                dispatch(Action.LoadTopics)
            }
        }
    }

    private inner class ExecutorImpl : 
        CoroutineExecutor<TopicsStore.Intent, Action, TopicsStore.State, Message, TopicsStore.Label>() {

        override fun executeIntent(intent: TopicsStore.Intent) {
            when (intent) {
                is TopicsStore.Intent.LoadTopics -> executeAction(Action.LoadTopics)
                is TopicsStore.Intent.SelectTopic -> selectTopic(intent.topic)
            }
        }

        override fun executeAction(action: Action) {
            when (action) {
                is Action.LoadTopics -> loadTopics()
            }
        }

        private fun loadTopics() {
            // Use cached data if available
            cachedTopics?.let { topics ->
                dispatch(Message.TopicsLoaded(topics))
                return
            }
            
            scope.launch {
                try {
                    dispatch(Message.LoadingStarted)
                    
                    val topics = withContext(Dispatchers.IO) {
                        phraseBookRepository.getTopics()
                    }
                    
                    cachedTopics = topics
                    dispatch(Message.TopicsLoaded(topics))
                } catch (e: Exception) {
                    dispatch(Message.ErrorOccurred(e.message ?: "Failed to load topics"))
                }
            }
        }

        private fun selectTopic(topic: Topic) {
            publish(TopicsStore.Label.NavigateToPhrases(topic))
        }
    }

    private object ReducerImpl : Reducer<TopicsStore.State, Message> {
        override fun TopicsStore.State.reduce(msg: Message): TopicsStore.State =
            when (msg) {
                is Message.LoadingStarted -> copy(
                    isLoading = true,
                    error = null
                )
                is Message.TopicsLoaded -> copy(
                    topics = msg.topics,
                    isLoading = false,
                    error = null
                )
                is Message.ErrorOccurred -> copy(
                    isLoading = false,
                    error = msg.error
                )
            }
    }
}