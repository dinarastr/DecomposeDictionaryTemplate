package ru.dinarastepina.ulchidictionary.presentation.components.phrasebook.topics

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.router.stack.ChildStack
import ru.dinarastepina.ulchidictionary.domain.model.Topic
import ru.dinarastepina.ulchidictionary.presentation.components.phrasebook.phrases.PhrasesComponent
import ru.dinarastepina.ulchidictionary.presentation.components.phrasebook.search.SearchComponent
import ru.dinarastepina.ulchidictionary.presentation.navigation.TabConfig

class DefaultTopicsComponent(
    componentContext: ComponentContext,
    private val topicsListComponentFactory: TopicsListComponent.Factory,
    private val phrasesComponentFactory: PhrasesComponent.Factory,
    private val searchComponentFactory: SearchComponent.Factory,
) : TopicsComponent, ComponentContext by componentContext {
    
    private val navigation = StackNavigation<TabConfig.Topics>()
    
    override val stack: Value<ChildStack<TabConfig.Topics, TopicsComponent.Child>> = childStack(
        source = navigation,
        serializer = TabConfig.Topics.serializer(),
        initialConfiguration = TabConfig.Topics.List,
        handleBackButton = true,
        childFactory = ::createChild,
        key = "TopicsStack"
    )
    
    private fun createChild(config: TabConfig.Topics, context: ComponentContext): TopicsComponent.Child =
        when (config) {
            is TabConfig.Topics.List -> TopicsComponent.Child.List(
                component = topicsListComponentFactory(
                    context,
                    ::navigateToPhrases,
                    ::navigateToSearch
                )
            )
            is TabConfig.Topics.Details -> TopicsComponent.Child.Details(
                component = phrasesComponentFactory(context, config.topic, ::navigateBack)
            )
            is TabConfig.Topics.Search -> TopicsComponent.Child.Search(
                component = searchComponentFactory(context, ::navigateBack)
            )
        }
    
    override fun navigateToPhrases(topic: Topic) {
        navigation.push(TabConfig.Topics.Details(topic))
    }

    override fun navigateToSearch() {
        navigation.push(TabConfig.Topics.Search)
    }
    
    override fun navigateBack() {
        navigation.pop()
    }
}