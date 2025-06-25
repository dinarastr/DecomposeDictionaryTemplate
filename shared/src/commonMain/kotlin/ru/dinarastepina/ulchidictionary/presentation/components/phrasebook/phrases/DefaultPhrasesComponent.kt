package ru.dinarastepina.ulchidictionary.presentation.components.phrasebook.phrases

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.operator.map
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import ru.dinarastepina.ulchidictionary.domain.model.Phrase
import ru.dinarastepina.ulchidictionary.domain.model.Topic
import ru.dinarastepina.ulchidictionary.presentation.store.PhrasesStore
import ru.dinarastepina.ulchidictionary.presentation.store.PhrasesStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.decompose.Cancellation
import com.arkivanov.essenty.lifecycle.doOnDestroy

class DefaultPhrasesComponent(
    componentContext: ComponentContext,
    private val topic: Topic,
    private val phrasesStoreFactory: PhrasesStoreFactory,
    private val onNavigateBack: () -> Unit
) : PhrasesComponent, ComponentContext by componentContext {
    
    private val store = phrasesStoreFactory.create(topic)
    private val scope = CoroutineScope(Dispatchers.Main)
    
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
        store.labels
            .onEach { label ->
                when (label) {
                    is PhrasesStore.Label.NavigateBack -> onNavigateBack()
                }
            }
            .launchIn(scope)
            
        // Add lifecycle cleanup for audio
        lifecycle.doOnDestroy {
            // Stop and release audio when component is destroyed
            store.accept(PhrasesStore.Intent.Release)
            scope.cancel()
        }
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
        // Stop audio before navigating back
        if (state.value.isPlaying) {
            store.accept(PhrasesStore.Intent.StopAudio)
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