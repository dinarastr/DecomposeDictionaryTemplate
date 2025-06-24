package com.dinarastepina.decomposedictionary.presentation.components.phrasebook.search

import com.arkivanov.decompose.ComponentContext

interface SearchComponent {
    
    fun interface Factory {
        operator fun invoke(componentContext: ComponentContext): SearchComponent
    }
}