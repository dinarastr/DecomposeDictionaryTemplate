package com.dinarastepina.decomposedictionary.presentation.components.main

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value
import com.dinarastepina.decomposedictionary.presentation.components.phrasebook.topics.TopicsComponent
import com.dinarastepina.decomposedictionary.presentation.components.dictionary.DictionaryComponent
import com.dinarastepina.decomposedictionary.presentation.navigation.TabConfig

interface MainComponent {
    val activeTab: Value<TabConfig>
    
    val slot: Value<ChildSlot<TabConfig, Child>>
    
    fun selectTab(tab: TabConfig)
    
    sealed class Child {
        data class Dictionary(val component: DictionaryComponent) : Child()
        data class Topics(val component: TopicsComponent) : Child()
    }
    
    fun interface Factory {
        operator fun invoke(componentContext: ComponentContext): MainComponent
    }
}