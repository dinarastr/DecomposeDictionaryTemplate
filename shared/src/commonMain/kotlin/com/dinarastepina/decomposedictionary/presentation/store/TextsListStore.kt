package com.dinarastepina.decomposedictionary.presentation.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.dinarastepina.decomposedictionary.domain.model.Text
import com.dinarastepina.decomposedictionary.domain.repository.TextsRepository
import kotlinx.coroutines.launch

interface TextsListStore : Store<TextsListStore.Intent, TextsListStore.State, TextsListStore.Label> {

    sealed class Intent {
        data object LoadTexts : Intent()
        data class SelectText(val text: Text) : Intent()
    }

    data class State(
        val texts: List<Text> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    )

    sealed class Label {
        data class NavigateToText(val text: Text) : Label()
    }
}

class TextsListStoreFactory(
    private val storeFactory: StoreFactory,
    private val textsRepository: TextsRepository
) {
    fun create(): TextsListStore =
        object : TextsListStore, Store<TextsListStore.Intent, TextsListStore.State, TextsListStore.Label> by storeFactory.create(
            name = "TextsListStore",
            initialState = TextsListStore.State(),
            bootstrapper = BootstrapperImpl(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed class Action {
        data object LoadTexts : Action()
    }

    private sealed class Message {
        data object LoadingStarted : Message()
        data class LoadingCompleted(val texts: List<Text>) : Message()
        data class ErrorOccurred(val error: String) : Message()
    }

    private class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            dispatch(Action.LoadTexts)
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<TextsListStore.Intent, Action, TextsListStore.State, Message, TextsListStore.Label>() {
        
        override fun executeIntent(intent: TextsListStore.Intent) {
            when (intent) {
                is TextsListStore.Intent.LoadTexts -> loadTexts()
                is TextsListStore.Intent.SelectText -> selectText(intent.text)
            }
        }

        override fun executeAction(action: Action) {
            when (action) {
                is Action.LoadTexts -> loadTexts()
            }
        }

        private fun loadTexts() {
            scope.launch {
                try {
                    dispatch(Message.LoadingStarted)
                    val texts = textsRepository.fetchAllTexts()
                    dispatch(Message.LoadingCompleted(texts))
                } catch (e: Exception) {
                    dispatch(Message.ErrorOccurred(e.message ?: "Failed to load texts"))
                }
            }
        }

        private fun selectText(text: Text) {
            publish(TextsListStore.Label.NavigateToText(text))
        }
    }

    private object ReducerImpl : Reducer<TextsListStore.State, Message> {
        override fun TextsListStore.State.reduce(message: Message): TextsListStore.State =
            when (message) {
                is Message.LoadingStarted -> copy(
                    isLoading = true,
                    error = null
                )
                is Message.LoadingCompleted -> copy(
                    texts = message.texts,
                    isLoading = false,
                    error = null
                )
                is Message.ErrorOccurred -> copy(
                    isLoading = false,
                    error = message.error
                )
            }
    }
} 