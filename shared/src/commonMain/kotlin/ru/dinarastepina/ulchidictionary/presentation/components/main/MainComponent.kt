package ru.dinarastepina.ulchidictionary.presentation.components.main

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value
import ru.dinarastepina.ulchidictionary.presentation.components.phrasebook.topics.TopicsComponent
import ru.dinarastepina.ulchidictionary.presentation.components.dictionary.DictionaryComponent
import ru.dinarastepina.ulchidictionary.presentation.components.texts.TextsComponent
import ru.dinarastepina.ulchidictionary.presentation.components.info.InfoComponent
import ru.dinarastepina.ulchidictionary.presentation.navigation.TabConfig

interface MainComponent {
    val activeTab: Value<TabConfig>
    
    val slot: Value<ChildSlot<TabConfig, Child>>
    
    fun selectTab(tab: TabConfig)
    
    sealed class Child {
        data class Dictionary(val component: DictionaryComponent) : Child()
        data class Topics(val component: TopicsComponent) : Child()
        data class Texts(val component: TextsComponent) : Child()
        data class Info(val component: InfoComponent) : Child()
    }
    
    fun interface Factory {
        operator fun invoke(componentContext: ComponentContext): MainComponent
    }
}