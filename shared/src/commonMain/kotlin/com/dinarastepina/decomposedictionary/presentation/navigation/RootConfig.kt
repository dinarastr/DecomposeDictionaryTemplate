package com.dinarastepina.decomposedictionary.presentation.navigation

import com.dinarastepina.decomposedictionary.domain.model.Topic
import kotlinx.serialization.Serializable

@Serializable
sealed class RootConfig {
    @Serializable
    data object Main : RootConfig()
}

@Serializable
sealed class TabConfig {
    @Serializable
    data object Dictionary : TabConfig()
    
    @Serializable
    sealed class Topics : TabConfig() {
        @Serializable
        data object List : Topics()
        
        @Serializable
        data class Details(val topic: Topic) : Topics()

        @Serializable
        data object Search : Topics()
    }

    @Serializable
    sealed class Texts : TabConfig() {
        @Serializable
        data object List : Texts()

        @Serializable
        data class Details(val textId: String) : Texts()
    }
}