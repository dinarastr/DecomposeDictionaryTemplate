package com.dinarastepina.decomposedictionary.presentation.components.texts

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.operator.map
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.dinarastepina.decomposedictionary.domain.model.Text
import com.dinarastepina.decomposedictionary.presentation.store.TextDetailsStore
import com.dinarastepina.decomposedictionary.presentation.store.TextDetailsStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.decompose.Cancellation
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.doOnDestroy

class DefaultTextDetailsComponent(
    componentContext: ComponentContext,
    textDetailsStoreFactory: TextDetailsStoreFactory,
    private val text: Text,
    private val onNavigateBack: () -> Unit
): TextDetailsComponent, ComponentContext by componentContext {

    private val store = textDetailsStoreFactory.create(text)
    private val scope = CoroutineScope(Dispatchers.Main)

    override val state: Value<TextDetailsComponent.State> = store.asValue().map { storeState ->
        TextDetailsComponent.State(
            text = storeState.text,
            sentencePairs = storeState.sentencePairs,
            isPlaying = storeState.isPlaying,
            error = storeState.error
        )
    }

    init {
        // Subscribe to navigation events from the store
        store.labels
            .onEach { label ->
                when (label) {
                    is TextDetailsStore.Label.NavigateBack -> onNavigateBack()
                }
            }
            .launchIn(scope)
            
        // Add lifecycle cleanup for audio
        lifecycle.doOnDestroy {
            // Stop and release audio when component is destroyed
            store.accept(TextDetailsStore.Intent.Release)
            scope.cancel()
        }
    }

    override fun onBackClick() {
        // Stop audio before navigating back
        if (state.value.isPlaying) {
            store.accept(TextDetailsStore.Intent.StopAudio)
        }
        onNavigateBack()
    }

    override fun onPlayAudio() {
        store.accept(TextDetailsStore.Intent.PlayAudio)
    }

    override fun onPauseAudio() {
        store.accept(TextDetailsStore.Intent.PauseAudio)
    }

    override fun onStopAudio() {
        store.accept(TextDetailsStore.Intent.StopAudio)
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