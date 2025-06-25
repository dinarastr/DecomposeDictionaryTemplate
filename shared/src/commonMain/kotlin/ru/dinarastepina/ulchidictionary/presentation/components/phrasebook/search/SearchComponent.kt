package ru.dinarastepina.ulchidictionary.presentation.components.phrasebook.search

import app.cash.paging.PagingData
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import ru.dinarastepina.ulchidictionary.domain.model.Phrase
import kotlinx.coroutines.flow.Flow

interface SearchComponent {
    val state: Value<State>
    val phrasesPagingFlow: Flow<PagingData<Phrase>>
    
    fun onSearchQuery(query: String)
    fun onClearSearch()
    fun onPlayAudio(phrase: Phrase)
    fun onPauseAudio()
    fun onStopAudio()
    fun onBackClick()
    
    data class State(
        val query: String = "",
        val currentlyPlayingPhrase: Phrase? = null,
        val isPlaying: Boolean = false,
        val error: String? = null
    )
    
    fun interface Factory {
        operator fun invoke(componentContext: ComponentContext, onNavigateBack: () -> Unit): SearchComponent
    }
}