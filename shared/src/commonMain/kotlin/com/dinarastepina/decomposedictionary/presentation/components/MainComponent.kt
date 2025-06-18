package com.dinarastepina.decomposedictionary.presentation.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.operator.map
import com.dinarastepina.decomposedictionary.presentation.navigation.TabConfig

/**
 * Main component that manages the bottom navigation tabs.
 */
interface MainComponent {
    // Expose the selected tab as a Value (observable)
    val activeTab: Value<TabConfig>
    
    // Expose the current active child component
    val slot: Value<ChildSlot<TabConfig, Child>>
    
    // Method to select a tab
    fun selectTab(tab: TabConfig)
    
    // Child types that represent each tab
    sealed class Child {
        data class Dictionary(val component: DictionaryComponent) : Child()
        data class Topics(val component: TopicsComponent) : Child()
        data class Lessons(val component: LessonsComponent) : Child()
    }
}

/**
 * Default implementation of the MainComponent interface.
 */
class DefaultMainComponent(
    componentContext: ComponentContext,
) : MainComponent, ComponentContext by componentContext {
    
    // Navigation controller for slot-based tab selection
    private val navigation = SlotNavigation<TabConfig>()
    
    // Initialize the slot with the Dictionary tab
    override val slot: Value<ChildSlot<TabConfig, MainComponent.Child>> = childSlot(
        source = navigation,
        serializer = TabConfig.serializer(),
        initialConfiguration = { TabConfig.Dictionary }, // Start with Dictionary tab
        handleBackButton = false, // Don't handle back button for tab selection
        childFactory = ::createChild,
        key = "MainTabSlot"
    )
    
    // Expose the selected tab by mapping from the slot
    override val activeTab: Value<TabConfig> = slot.map { childSlot ->
        childSlot.child?.configuration ?: TabConfig.Dictionary
    }
    
    // Method to select a tab
    override fun selectTab(tab: TabConfig) {
        navigation.activate(tab)
    }
    
    // Factory method to create child components based on tab configuration
    private fun createChild(config: TabConfig, context: ComponentContext): MainComponent.Child =
        when (config) {
            is TabConfig.Dictionary -> MainComponent.Child.Dictionary(
                component = DefaultDictionaryComponent(context)
            )
            is TabConfig.Topics -> MainComponent.Child.Topics(
                component = DefaultTopicsComponent(context)
            )
            is TabConfig.Lessons -> MainComponent.Child.Lessons(
                component = DefaultLessonsComponent(context)
            )
        }
}