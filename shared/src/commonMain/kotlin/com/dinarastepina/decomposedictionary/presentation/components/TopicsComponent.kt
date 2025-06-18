package com.dinarastepina.decomposedictionary.presentation.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.dinarastepina.decomposedictionary.presentation.navigation.TabConfig

/**
 * Topics component that manages navigation between topics list and topic details.
 */
interface TopicsComponent {
    // Expose the navigation stack as a Value (observable)
    val stack: Value<ChildStack<TabConfig.Topics, Child>>
    
    // Navigate to topic details
    fun navigateToTopicDetails(topicId: String)
    
    // Navigate back
    fun navigateBack()
    
    // Child types that can be in the stack
    sealed class Child {
        // Topics list screen
        data class List(val component: TopicsListComponent) : Child()
        
        // Topic details screen
        data class Details(val component: TopicDetailsComponent) : Child()
    }
}

/**
 * Default implementation of the TopicsComponent interface.
 */
class DefaultTopicsComponent(
    componentContext: ComponentContext,
    private val storeFactory: StoreFactory = DefaultStoreFactory(),
) : TopicsComponent, ComponentContext by componentContext {
    
    // Navigation controller for the topics stack
    private val navigation = StackNavigation<TabConfig.Topics>()
    
    // Initialize the navigation stack with the Topics list
    override val stack: Value<ChildStack<TabConfig.Topics, TopicsComponent.Child>> = childStack(
        source = navigation,
        serializer = TabConfig.Topics.serializer(),
        initialConfiguration = TabConfig.Topics.List, // Start with the list
        handleBackButton = true, // Handle system back button
        childFactory = ::createChild,
        key = "TopicsStack"
    )
    
    // Factory method to create child components based on configuration
    private fun createChild(config: TabConfig.Topics, context: ComponentContext): TopicsComponent.Child =
        when (config) {
            is TabConfig.Topics.List -> TopicsComponent.Child.List(
                component = DefaultTopicsListComponent(
                    componentContext = context,
                    storeFactory = storeFactory,
                    onTopicSelected = ::navigateToTopicDetails
                )
            )
            is TabConfig.Topics.Details -> TopicsComponent.Child.Details(
                component = DefaultTopicDetailsComponent(
                    componentContext = context,
                    storeFactory = storeFactory,
                    topicId = config.topicId
                )
            )
        }
    
    // Navigate to topic details
    override fun navigateToTopicDetails(topicId: String) {
        navigation.push(TabConfig.Topics.Details(topicId))
    }
    
    // Navigate back
    override fun navigateBack() {
        navigation.pop()
    }
}

/**
 * Component for displaying the list of topics.
 */
interface TopicsListComponent {
    // Add methods and properties as needed
}

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

/**
 * Component for displaying topic details.
 */
interface TopicDetailsComponent {
    // Add methods and properties as needed
}

/**
 * Default implementation of the TopicDetailsComponent interface.
 */
class DefaultTopicDetailsComponent(
    componentContext: ComponentContext,
    private val storeFactory: StoreFactory,
    private val topicId: String
) : TopicDetailsComponent, ComponentContext by componentContext {
    // Implement topic details functionality using MVIKotlin
}