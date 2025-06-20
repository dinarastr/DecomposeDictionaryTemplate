package com.dinarastepina.decomposedictionary.presentation.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.dinarastepina.decomposedictionary.presentation.navigation.RootConfig
import com.dinarastepina.decomposedictionary.presentation.components.MainComponent
import com.dinarastepina.decomposedictionary.presentation.components.DefaultMainComponent

/**
 * Root component that manages the main navigation stack of the application.
 * This is the top-level component that contains all other components.
 */
interface RootComponent {
    // Expose the navigation stack as a Value (observable)
    val stack: Value<ChildStack<RootConfig, Child>>
    
    // Child types that can be in the stack
    sealed class Child {
        // Main screen with bottom navigation
        data class Main(val component: MainComponent) : Child()
    }
    
    // Factory interface for creating RootComponent instances
    fun interface Factory {
        operator fun invoke(componentContext: ComponentContext): RootComponent
    }
}

/**
 * Default implementation of the RootComponent interface.
 */
class DefaultRootComponent(
    componentContext: ComponentContext,
    private val mainComponentFactory: MainComponent.Factory,
) : RootComponent, ComponentContext by componentContext {
    
    // Navigation controller for the root stack
    private val navigation = StackNavigation<RootConfig>()
    
    // Initialize the navigation stack with the Main screen
    override val stack: Value<ChildStack<RootConfig, RootComponent.Child>> = childStack(
        source = navigation,
        serializer = RootConfig.serializer(),
        initialConfiguration = RootConfig.Main, // Start with the Main screen
        handleBackButton = true, // Handle system back button
        childFactory = ::createChild,
        key = "RootStack"
    )
    
    // Factory method to create child components based on configuration
    private fun createChild(config: RootConfig, context: ComponentContext): RootComponent.Child =
        when (config) {
            is RootConfig.Main -> RootComponent.Child.Main(
                component = mainComponentFactory(context)
            )
        }
    
    // Factory implementation
    class Factory(
        private val mainComponentFactory: MainComponent.Factory,
    ) : RootComponent.Factory {
        override fun invoke(componentContext: ComponentContext): RootComponent =
            DefaultRootComponent(
                componentContext = componentContext,
                mainComponentFactory = mainComponentFactory
            )
    }
}