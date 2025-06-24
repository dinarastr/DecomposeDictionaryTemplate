package com.dinarastepina.decomposedictionary.presentation.components.info

import com.arkivanov.decompose.ComponentContext

interface InfoComponent {

    fun interface Factory {
        operator fun invoke(componentContext: ComponentContext): InfoComponent
    }
} 