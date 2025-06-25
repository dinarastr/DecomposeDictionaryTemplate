package ru.dinarastepina.ulchidictionary.presentation.navigation

import ru.dinarastepina.ulchidictionary.domain.model.Topic
import kotlinx.serialization.Serializable
import ru.dinarastepina.ulchidictionary.domain.model.Text

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
        data class Details(val text: Text) : Texts()
    }

    @Serializable
    data object Info : TabConfig()
}