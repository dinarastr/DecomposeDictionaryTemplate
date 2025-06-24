package com.dinarastepina.decomposedictionary.presentation.components.phrasebook.topics

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.dinarastepina.decomposedictionary.domain.model.Topic

interface TopicsListComponent {

    val state: Value<State>

    data class State(
        val topics: List<Topic> = emptyList(),
        val error: String? = null
    )

    fun onTopicSelected(topicId: Int, topicName: String)

    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onTopicSelected: (Topic) -> Unit,
            onSearchBarClicked: () -> Unit
        ): TopicsListComponent
    }
}