package com.dinarastepina.decomposedictionary.presentation.components.info

import com.arkivanov.decompose.ComponentContext

interface InfoComponent {
    // Info component is stateless, just displays static content
    
    fun interface Factory {
        operator fun invoke(componentContext: ComponentContext): InfoComponent
    }
} 