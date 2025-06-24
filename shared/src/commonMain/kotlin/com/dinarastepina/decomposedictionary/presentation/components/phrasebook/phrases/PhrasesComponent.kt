package com.dinarastepina.decomposedictionary.presentation.components.phrasebook.phrases

import com.arkivanov.decompose.ComponentContext

/**
 * Component for displaying topic details.
 */
interface PhrasesComponent {
    // Add methods and properties as needed
    
    fun interface Factory {
        operator fun invoke(componentContext: ComponentContext, topicId: String): PhrasesComponent
    }
}

