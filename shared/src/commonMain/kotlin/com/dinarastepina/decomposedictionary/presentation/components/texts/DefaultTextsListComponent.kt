package com.dinarastepina.decomposedictionary.presentation.components.texts

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.operator.map
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.dinarastepina.decomposedictionary.domain.model.Text
import com.dinarastepina.decomposedictionary.presentation.store.TextsListStore
import com.dinarastepina.decomposedictionary.presentation.store.TextsListStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.decompose.Cancellation

class DefaultTextsListComponent(
    componentContext: ComponentContext,
    private val textsListStoreFactory: TextsListStoreFactory,
    private val onTextSelected: (Text) -> Unit
): TextsListComponent, ComponentContext by componentContext {

    private val store = textsListStoreFactory.create()

    override val state: Value<TextsListComponent.State> = store.asValue().map { storeState ->
        TextsListComponent.State(
            texts = storeState.texts,
            isLoading = storeState.isLoading,
            error = storeState.error
        )
    }

    init {
        // Subscribe to navigation events from the store
        val scope = CoroutineScope(Dispatchers.Main)
        store.labels
            .onEach { label ->
                when (label) {
                    is TextsListStore.Label.NavigateToText -> onTextSelected(label.text)
                }
            }
            .launchIn(scope)
    }

    override fun onTextClick(text: Text) {
        store.accept(TextsListStore.Intent.SelectText(text))
    }

    private fun <T : Any> Store<*, T, *>.asValue(): Value<T> {
        val scope = CoroutineScope(Dispatchers.Main)
        val stateFlow = stateFlow(scope)
        return object : Value<T>() {
            override val value: T get() = stateFlow.value

            override fun subscribe(observer: (T) -> Unit): Cancellation {
                val job = stateFlow
                    .onEach(observer)
                    .launchIn(scope)

                return Cancellation {
                    job.cancel()
                    scope.cancel()
                }
            }
        }
    }
} 