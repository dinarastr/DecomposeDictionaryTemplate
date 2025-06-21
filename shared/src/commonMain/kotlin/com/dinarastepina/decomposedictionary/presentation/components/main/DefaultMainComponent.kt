package com.dinarastepina.decomposedictionary.presentation.components.main

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.operator.map
import com.dinarastepina.decomposedictionary.presentation.components.LessonsComponent
import com.dinarastepina.decomposedictionary.presentation.components.TopicsComponent
import com.dinarastepina.decomposedictionary.presentation.components.dictionary.DictionaryComponent
import com.dinarastepina.decomposedictionary.presentation.navigation.TabConfig

class DefaultMainComponent(
    componentContext: ComponentContext,
    private val dictionaryComponentFactory: DictionaryComponent.Factory,
    private val topicsComponentFactory: TopicsComponent.Factory,
    private val lessonsComponentFactory: LessonsComponent.Factory,
) : MainComponent, ComponentContext by componentContext {
    
    private val navigation = SlotNavigation<TabConfig>()
    
    override val slot: Value<ChildSlot<TabConfig, MainComponent.Child>> = childSlot(
        source = navigation,
        serializer = TabConfig.serializer(),
        initialConfiguration = { TabConfig.Dictionary },
        handleBackButton = false,
        childFactory = ::createChild,
        key = "MainTabSlot"
    )
    
    override val activeTab: Value<TabConfig> = slot.map { childSlot ->
        childSlot.child?.configuration ?: TabConfig.Dictionary
    }
    
    override fun selectTab(tab: TabConfig) {
        navigation.activate(tab)
    }
    
    private fun createChild(config: TabConfig, context: ComponentContext): MainComponent.Child =
        when (config) {
            is TabConfig.Dictionary -> MainComponent.Child.Dictionary(
                component = dictionaryComponentFactory(context)
            )
            is TabConfig.Topics -> MainComponent.Child.Topics(
                component = topicsComponentFactory(context)
            )
            is TabConfig.Lessons -> MainComponent.Child.Lessons(
                component = lessonsComponentFactory(context)
            )
        }
    
    class Factory(
        private val dictionaryComponentFactory: DictionaryComponent.Factory,
        private val topicsComponentFactory: TopicsComponent.Factory,
        private val lessonsComponentFactory: LessonsComponent.Factory,
    ) : MainComponent.Factory {
        override fun invoke(componentContext: ComponentContext): MainComponent =
            DefaultMainComponent(
                componentContext = componentContext,
                dictionaryComponentFactory = dictionaryComponentFactory,
                topicsComponentFactory = topicsComponentFactory,
                lessonsComponentFactory = lessonsComponentFactory
            )
    }
}