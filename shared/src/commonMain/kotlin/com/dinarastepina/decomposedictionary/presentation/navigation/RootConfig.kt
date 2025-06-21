package com.dinarastepina.decomposedictionary.presentation.navigation

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
        data class Details(val topicId: String) : Topics()
    }
    
    @Serializable
    sealed class Lessons : TabConfig() {
        @Serializable
        data object List : Lessons()
        
        @Serializable
        data class Details(val lessonId: String) : Lessons()
        
        @Serializable
        data class Section(val lessonId: String, val sectionId: String) : Lessons()
    }
}