package ru.dinarastepina.ulchidictionary.presentation.components.texts

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import ru.dinarastepina.ulchidictionary.presentation.navigation.TabConfig

class DefaultTextsComponent(
    componentContext: ComponentContext,
    private val textsListComponentFactory: TextsListComponent.Factory,
    private val textDetailsComponentFactory: TextDetailsComponent.Factory
) : TextsComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<TabConfig.Texts>()

    override val stack: Value<ChildStack<TabConfig.Texts, TextsComponent.Child>> = childStack(
        source = navigation,
        serializer = TabConfig.Texts.serializer(),
        initialConfiguration = TabConfig.Texts.List,
        handleBackButton = true,
        childFactory = ::child
    )

    private fun child(
        config: TabConfig.Texts,
        componentContext: ComponentContext
    ): TextsComponent.Child = when (config) {
        is TabConfig.Texts.List -> TextsComponent.Child.List(
            textsListComponentFactory(
                componentContext = componentContext,
                onTextSelected = { text ->
                    navigation.push(TabConfig.Texts.Details(text))
                }
            )
        )

        is TabConfig.Texts.Details -> TextsComponent.Child.Details(
            textDetailsComponentFactory(
                componentContext = componentContext,
                text = config.text,
                onNavigateBack = { navigation.pop() }
            )
        )
    }
} 