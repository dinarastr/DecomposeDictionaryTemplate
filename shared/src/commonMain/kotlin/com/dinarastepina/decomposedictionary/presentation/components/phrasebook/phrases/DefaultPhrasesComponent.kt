package com.dinarastepina.decomposedictionary.presentation.components.phrasebook.phrases

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

/**
 * Default implementation of PhrasesComponent.
 * Handles business logic for displaying phrases of a specific topic.
 */
class DefaultPhrasesComponent(
    componentContext: ComponentContext,
    private val topicId: String
) : PhrasesComponent, ComponentContext by componentContext {
    
    private val _state = MutableValue(
        PhrasesComponent.State(
            topicId = topicId,
            topicName = "Topic $topicId", // This should come from repository
            phrases = emptyList(), // This should be loaded from repository
            error = null
        )
    )
    
    override val state: Value<PhrasesComponent.State> = _state
    
    // TODO: Add methods to load phrases from repository
    // TODO: Add phrase selection handling
    // TODO: Add search functionality if needed
}