package com.dinarastepina.decomposedictionary.presentation.components.phrasebook.topics

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.dinarastepina.decomposedictionary.domain.model.Topic
import com.dinarastepina.decomposedictionary.presentation.components.phrasebook.phrases.PhrasesComponent
import com.dinarastepina.decomposedictionary.presentation.components.phrasebook.search.SearchComponent
import com.dinarastepina.decomposedictionary.presentation.navigation.TabConfig

interface TopicsComponent {
    val stack: Value<ChildStack<TabConfig.Topics, Child>>
    
    fun navigateToPhrases(topic: Topic)

    fun navigateToSearch()

    fun navigateBack()
    
    sealed class Child {
        data class List(val component: TopicsListComponent) : Child()
        
        data class Details(val component: PhrasesComponent) : Child()

        data class Search(val component: SearchComponent) : Child()
    }
    
    fun interface Factory {
        operator fun invoke(componentContext: ComponentContext): TopicsComponent
    }
}