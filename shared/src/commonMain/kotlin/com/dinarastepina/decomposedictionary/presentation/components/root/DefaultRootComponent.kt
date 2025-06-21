package com.dinarastepina.decomposedictionary.presentation.components.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.dinarastepina.decomposedictionary.presentation.components.main.MainComponent
import com.dinarastepina.decomposedictionary.presentation.navigation.RootConfig

class DefaultRootComponent(
    componentContext: ComponentContext,
    private val mainComponentFactory: MainComponent.Factory,
) : RootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<RootConfig>()

    override val stack: Value<ChildStack<RootConfig, RootComponent.Child>> = childStack(
        source = navigation,
        serializer = RootConfig.serializer(),
        initialConfiguration = RootConfig.Main,
        handleBackButton = true,
        childFactory = ::createChild,
        key = "RootStack"
    )

    private fun createChild(config: RootConfig, context: ComponentContext): RootComponent.Child =
        when (config) {
            is RootConfig.Main -> RootComponent.Child.Main(
                component = mainComponentFactory(context)
            )
        }

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