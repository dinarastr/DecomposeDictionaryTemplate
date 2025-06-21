package com.dinarastepina.decomposedictionary.presentation.components.dictionary

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.operator.map
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.dinarastepina.decomposedictionary.presentation.store.DictionaryStore
import com.dinarastepina.decomposedictionary.presentation.store.DictionaryStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.decompose.Cancellation

/**
 * Default implementation of the DictionaryComponent interface.
 */
class DefaultDictionaryComponent(
    componentContext: ComponentContext,
    private val storeFactory: DictionaryStoreFactory,
) : DictionaryComponent, ComponentContext by componentContext {

    // Create the store
    private val store = storeFactory.create()

    // Map the store state to the component state
    override val state: Value<DictionaryComponent.State> = store.asValue().map { storeState ->
        DictionaryComponent.State(
            query = storeState.query,
            words = storeState.words,
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

    // Helper extension to convert a Store to a Value
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
    
    // Factory implementation
    class Factory(
        private val storeFactory: DictionaryStoreFactory,
    ) : DictionaryComponent.Factory {
        override fun invoke(componentContext: ComponentContext): DictionaryComponent =
            DefaultDictionaryComponent(
                componentContext = componentContext,
                storeFactory = storeFactory
            )
    }
}