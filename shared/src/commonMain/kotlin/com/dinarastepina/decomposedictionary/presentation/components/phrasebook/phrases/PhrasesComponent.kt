package com.dinarastepina.decomposedictionary.presentation.components.phrasebook.phrases

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.dinarastepina.decomposedictionary.domain.model.Phrase
import com.dinarastepina.decomposedictionary.domain.model.Topic

/**
 * Component for displaying topic details.
 */
interface PhrasesComponent {
    val state: Value<State>
    
    fun onPlayAudio(phrase: Phrase)
    fun onPauseAudio()
    fun onStopAudio()
    fun onBackClick()
    
    data class State(
        val topic: Topic,
        val phrases: List<Phrase> = emptyList(),
        val currentlyPlayingPhrase: Phrase? = null,
        val isPlaying: Boolean = false,
        val isLoading: Boolean = false,
        val error: String? = null
    )
    
    fun interface Factory {
        operator fun invoke(componentContext: ComponentContext, topic: Topic, onNavigateBack: () -> Unit): PhrasesComponent
    }
}

