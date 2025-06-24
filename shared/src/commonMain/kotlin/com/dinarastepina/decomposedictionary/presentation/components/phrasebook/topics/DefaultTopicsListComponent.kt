package com.dinarastepina.decomposedictionary.presentation.components.phrasebook.topics

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.store.StoreFactory

/**
 * Default implementation of the TopicsListComponent interface.
 */
class DefaultTopicsListComponent(
    componentContext: ComponentContext,
    private val storeFactory: StoreFactory,
    private val onTopicSelected: (String) -> Unit
) : TopicsListComponent, ComponentContext by componentContext {
    // Implement topics list functionality using MVIKotlin


}