package ru.dinarastepina.ulchidictionary.presentation.components.phrasebook.topics

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import ru.dinarastepina.ulchidictionary.domain.model.Topic

interface TopicsListComponent {

    val state: Value<State>

    data class State(
        val topics: List<Topic> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    )

    fun onTopicSelected(topicId: Int, topicName: String)
    fun onSearchClicked()

    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onTopicSelected: (Topic) -> Unit,
            onSearchBarClicked: () -> Unit
        ): TopicsListComponent
    }
}