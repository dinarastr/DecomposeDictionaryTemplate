package com.dinarastepina.decomposedictionary.presentation.components.phrasebook.topics

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.operator.map
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.dinarastepina.decomposedictionary.domain.model.Topic
import com.dinarastepina.decomposedictionary.presentation.store.TopicsStore
import com.dinarastepina.decomposedictionary.presentation.store.TopicsStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach

/**
 * Default implementation of the TopicsListComponent interface.
 */
class DefaultTopicsListComponent(
    componentContext: ComponentContext,
    private val topicsStoreFactory: TopicsStoreFactory,
    private val onTopicSelected: (Topic) -> Unit,
    private val onSearchBarClicked: () -> Unit
) : TopicsListComponent, ComponentContext by componentContext {
    
    private val store = topicsStoreFactory.create()
    
    override val state: Value<TopicsListComponent.State> = store.asValue().map { storeState ->
        TopicsListComponent.State(
            topics = storeState.topics,
            error = storeState.error
        )
    }
    
    init {
        // Subscribe to navigation events from the store
        val scope = CoroutineScope(Dispatchers.Main)
        store.labels
            .mapNotNull { label ->
                when (label) {
                    is TopicsStore.Label.NavigateToPhrases -> label.topic
                }
            }
            .onEach { topic ->
                onTopicSelected(topic)
            }
            .launchIn(scope)
    }
    
    override fun onTopicSelected(topicId: Int, topicName: String) {
        val topic = state.value.topics.find { it.id == topicId }
        if (topic != null) {
            store.accept(TopicsStore.Intent.SelectTopic(topic))
        }
    }
    
    private fun TopicsStore.asValue(): Value<TopicsStore.State> {
        val scope = CoroutineScope(Dispatchers.Main)
        val stateFlow = store.stateFlow(scope)
        return object : Value<TopicsStore.State>() {
            override val value: TopicsStore.State get() = stateFlow.value

            override fun subscribe(observer: (TopicsStore.State) -> Unit): com.arkivanov.decompose.Cancellation {
                val job = stateFlow
                    .onEach(observer)
                    .launchIn(scope)

                return com.arkivanov.decompose.Cancellation {
                    job.cancel()
                }
            }
        }
    }
}