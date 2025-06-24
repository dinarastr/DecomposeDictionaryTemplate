package com.dinarastepina.decomposedictionary.presentation.components.phrasebook.phrases

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.store.StoreFactory

class DefaultPhrasesComponent(
    componentContext: ComponentContext,
    private val storeFactory: StoreFactory,
    private val topicId: String
) : PhrasesComponent, ComponentContext by componentContext {
    // Implement topic details functionality using MVIKotlin
}