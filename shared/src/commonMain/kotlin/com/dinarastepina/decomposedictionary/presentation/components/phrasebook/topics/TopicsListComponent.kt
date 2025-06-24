package com.dinarastepina.decomposedictionary.presentation.components.phrasebook.topics

import com.arkivanov.decompose.ComponentContext

interface TopicsListComponent {
    // Add methods and properties as needed

    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext, 
            onTopicSelected: (String) -> Unit
        ): TopicsListComponent
    }
}