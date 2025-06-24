package com.dinarastepina.decomposedictionary.presentation.components.phrasebook.phrases

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.operator.map
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.dinarastepina.decomposedictionary.domain.model.Phrase
import com.dinarastepina.decomposedictionary.domain.model.Topic
import com.dinarastepina.decomposedictionary.presentation.store.PhrasesStore
import com.dinarastepina.decomposedictionary.presentation.store.PhrasesStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.decompose.Cancellation

/**
 * Default implementation of PhrasesComponent.
 * Handles business logic for displaying phrases of a specific topic.
 */
class DefaultPhrasesComponent(
    componentContext: ComponentContext,
    private val topic: Topic,
    private val phrasesStoreFactory: PhrasesStoreFactory,
    private val onNavigateBack: () -> Unit
) : PhrasesComponent, ComponentContext by componentContext {
    
    private val store = phrasesStoreFactory.create(topic)
    
    override val state: Value<PhrasesComponent.State> = store.asValue().map { storeState ->
        PhrasesComponent.State(
            topic = storeState.topic,
            phrases = storeState.phrases,
            currentlyPlayingPhrase = storeState.currentlyPlayingPhrase,
            isPlaying = storeState.isPlaying,
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
                    is PhrasesStore.Label.NavigateBack -> onNavigateBack()
                }
            }
            .launchIn(scope)
    }
    
    override fun onPlayAudio(phrase: Phrase) {
        store.accept(PhrasesStore.Intent.PlayAudio(phrase))
    }
    
    override fun onPauseAudio() {
        store.accept(PhrasesStore.Intent.PauseAudio)
    }
    
    override fun onStopAudio() {
        store.accept(PhrasesStore.Intent.StopAudio)
    }
    
    override fun onBackClick() {
        onNavigateBack()
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