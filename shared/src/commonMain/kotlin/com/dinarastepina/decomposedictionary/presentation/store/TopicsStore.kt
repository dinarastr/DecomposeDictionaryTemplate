package com.dinarastepina.decomposedictionary.presentation.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.dinarastepina.decomposedictionary.domain.model.Topic
import com.dinarastepina.decomposedictionary.domain.repository.PhraseBookRepository
import kotlinx.coroutines.launch

interface TopicsStore : Store<TopicsStore.Intent, TopicsStore.State, TopicsStore.Label> {

    sealed class Intent {
        data object LoadTopics : Intent()
        data class SelectTopic(val topic: Topic) : Intent()
    }

    data class State(
        val topics: List<Topic> = emptyList(),
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
    
    fun create(): TopicsStore =
        object : TopicsStore, Store<TopicsStore.Intent, TopicsStore.State, TopicsStore.Label> by storeFactory.create(
            name = "TopicsStore",
            initialState = TopicsStore.State(),
            bootstrapper = BootstrapperImpl(),
            executorFactory = { ExecutorImpl() },
            reducer = ReducerImpl
        ) {}

    private sealed class Action {
        data object LoadTopics : Action()
    }

    private sealed class Message {
        data class TopicsLoaded(val topics: List<Topic>) : Message()
        data class ErrorOccurred(val error: String) : Message()
    }

    private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            dispatch(Action.LoadTopics)
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
            scope.launch {
                try {
                    val topics = phraseBookRepository.getTopics()
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
        override fun TopicsStore.State.reduce(message: Message): TopicsStore.State =
            when (message) {
                is Message.TopicsLoaded -> copy(
                    topics = message.topics,
                    error = null
                )
                is Message.ErrorOccurred -> copy(
                    error = message.error
                )
            }
    }
}