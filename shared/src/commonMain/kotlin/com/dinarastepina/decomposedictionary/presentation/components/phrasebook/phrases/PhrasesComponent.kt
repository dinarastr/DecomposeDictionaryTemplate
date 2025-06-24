package com.dinarastepina.decomposedictionary.presentation.components.phrasebook.phrases

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.dinarastepina.decomposedictionary.domain.model.Phrase

/**
 * Component for displaying topic details.
 */
interface PhrasesComponent {
    val state: Value<State>
    
    data class State(
        val topicId: String = "",
        val topicName: String = "",
        val phrases: List<Phrase> = emptyList(),
        val error: String? = null
    )
    
    fun interface Factory {
        operator fun invoke(componentContext: ComponentContext, topicId: String): PhrasesComponent
    }
}

