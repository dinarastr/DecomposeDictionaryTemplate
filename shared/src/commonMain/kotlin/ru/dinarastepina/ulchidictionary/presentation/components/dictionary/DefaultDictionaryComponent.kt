package ru.dinarastepina.ulchidictionary.presentation.components.dictionary

import app.cash.paging.PagingData
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.operator.map
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import ru.dinarastepina.ulchidictionary.domain.model.Word
import ru.dinarastepina.ulchidictionary.presentation.store.DictionaryStore
import ru.dinarastepina.ulchidictionary.presentation.store.DictionaryStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.decompose.Cancellation
import ru.dinarastepina.ulchidictionary.domain.model.LANGUAGE

class DefaultDictionaryComponent(
    componentContext: ComponentContext,
    private val storeFactory: DictionaryStoreFactory
) : DictionaryComponent, ComponentContext by componentContext {

    private val store = storeFactory.create()

    override val state: Value<DictionaryComponent.State> = store.asValue().map { storeState ->
        DictionaryComponent.State(
            query = storeState.query,
            error = storeState.error,
            isInitialized = storeState.isInitialized,
            targetLanguage = storeState.targetLanguage,
            selectedLanguage = storeState.selectedLanguage
        )
    }
    
    override val navigationEvents: Flow<Word> = store.labels.mapNotNull { label ->
        when (label) {
            is DictionaryStore.Label.NavigateToWordDetail -> label.word
        }
    }

    override val wordsPagingFlow: Flow<PagingData<Word>> = store.wordsPagingFlow

    override fun search(query: String) {
        store.accept(DictionaryStore.Intent.Search(query))
    }

    override fun clearSearch() {
        store.accept(DictionaryStore.Intent.ClearSearch)
    }

    override fun changeLanguage(language: LANGUAGE) {
        store.accept(DictionaryStore.Intent.UpdateSelectedLanguage(language))
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
    
    class Factory(
        private val storeFactory: DictionaryStoreFactory
    ) : DictionaryComponent.Factory {
        override fun invoke(componentContext: ComponentContext): DictionaryComponent =
            DefaultDictionaryComponent(
                componentContext = componentContext,
                storeFactory = storeFactory
            )
    }
}