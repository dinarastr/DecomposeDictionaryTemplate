package com.dinarastepina.decomposedictionary.presentation.components.info

import com.arkivanov.decompose.ComponentContext

class DefaultInfoComponent(
    componentContext: ComponentContext
) : InfoComponent, ComponentContext by componentContext {
    // Stateless component - no store needed
} 