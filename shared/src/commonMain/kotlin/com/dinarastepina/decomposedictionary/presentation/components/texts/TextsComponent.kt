package com.dinarastepina.decomposedictionary.presentation.components.texts

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.dinarastepina.decomposedictionary.domain.model.Text
import com.dinarastepina.decomposedictionary.presentation.navigation.TabConfig

interface TextsComponent {
    val stack: Value<ChildStack<TabConfig.Texts, Child>>
    
    sealed class Child {
        data class List(val component: TextsListComponent) : Child()
        data class Details(val component: TextDetailsComponent) : Child()
    }
    
    fun interface Factory {
        operator fun invoke(componentContext: ComponentContext): TextsComponent
    }
}

interface TextsListComponent {
    val state: Value<State>
    
    fun onTextClick(text: Text)
    
    data class State(
        val texts: List<Text> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    )
    
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onTextSelected: (Text) -> Unit
        ): TextsListComponent
    }
}

interface TextDetailsComponent {
    val state: Value<State>
    
    fun onBackClick()
    fun onPlayAudio()
    fun onPauseAudio()
    fun onStopAudio()
    
    data class State(
        val text: Text,
        val sentencePairs: List<SentencePair> = emptyList(),
        val isPlaying: Boolean = false,
        val error: String? = null
    )
    
    data class SentencePair(
        val index: Int,
        val ulchi: String,
        val russian: String
    )
    
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            text: Text,
            onNavigateBack: () -> Unit
        ): TextDetailsComponent
    }
} 