package com.dinarastepina.decomposedictionary.presentation.components.phrasebook.search

import app.cash.paging.PagingData
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.operator.map
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.dinarastepina.decomposedictionary.domain.model.Phrase
import com.dinarastepina.decomposedictionary.presentation.store.SearchStore
import com.dinarastepina.decomposedictionary.presentation.store.SearchStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.decompose.Cancellation
import com.arkivanov.essenty.lifecycle.doOnDestroy

class DefaultSearchComponent(
    componentContext: ComponentContext,
    private val searchStoreFactory: SearchStoreFactory,
    private val onNavigateBack: () -> Unit
): SearchComponent, ComponentContext by componentContext {

    private val store = searchStoreFactory.create()
    private val scope = CoroutineScope(Dispatchers.Main)

    override val state: Value<SearchComponent.State> = store.asValue().map { storeState ->
        SearchComponent.State(
            query = storeState.query,
            currentlyPlayingPhrase = storeState.currentlyPlayingPhrase,
            isPlaying = storeState.isPlaying,
            error = storeState.error
        )
    }

    override val phrasesPagingFlow: Flow<PagingData<Phrase>> = store.phrasesPagingFlow

    init {
        // Subscribe to navigation events from the store
        store.labels
            .onEach { label ->
                when (label) {
                    is SearchStore.Label.NavigateBack -> onNavigateBack()
                }
            }
            .launchIn(scope)
            
        // Add lifecycle cleanup for audio
        lifecycle.doOnDestroy {
            // Stop and release audio when component is destroyed
            store.accept(SearchStore.Intent.Release)
            scope.cancel()
        }
    }

    override fun onSearchQuery(query: String) {
        store.accept(SearchStore.Intent.Search(query))
    }

    override fun onClearSearch() {
        store.accept(SearchStore.Intent.ClearSearch)
    }

    override fun onPlayAudio(phrase: Phrase) {
        store.accept(SearchStore.Intent.PlayAudio(phrase))
    }

    override fun onPauseAudio() {
        store.accept(SearchStore.Intent.PauseAudio)
    }

    override fun onStopAudio() {
        store.accept(SearchStore.Intent.StopAudio)
    }

    override fun onBackClick() {
        // Stop audio before navigating back
        if (state.value.isPlaying) {
            store.accept(SearchStore.Intent.StopAudio)
        }
        onNavigateBack()
    }

    private fun <T : Any> Store<*, T, *>.asValue(): Value<T> {
        val stateFlow = stateFlow(scope)
        return object : Value<T>() {
            override val value: T get() = stateFlow.value

            override fun subscribe(observer: (T) -> Unit): Cancellation {
                val job = stateFlow
                    .onEach(observer)
                    .launchIn(scope)

                return Cancellation {
                    job.cancel()
                }
            }
        }
    }
}