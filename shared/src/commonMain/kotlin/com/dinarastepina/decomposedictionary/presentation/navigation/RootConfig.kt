package com.dinarastepina.decomposedictionary.presentation.navigation

import com.arkivanov.decompose.ComponentContext
import kotlinx.serialization.Serializable

// Root level configurations
@Serializable
sealed class RootConfig {
    // Main screen with bottom navigation
    @Serializable
    data object Main : RootConfig()
}

// Main screen tab configurations
@Serializable
sealed class TabConfig {
    // Dictionary tab
    @Serializable
    data object Dictionary : TabConfig()
    
    // Topics tab with nested navigation
    @Serializable
    sealed class Topics : TabConfig() {
        // Topics list screen
        @Serializable
        data object List : Topics()
        
        // Topic details screen
        @Serializable
        data class Details(val topicId: String) : Topics()
    }
    
    // Lessons tab with nested navigation
    @Serializable
    sealed class Lessons : TabConfig() {
        // Lessons list screen
        @Serializable
        data object List : Lessons()
        
        // Lesson details screen with sections
        @Serializable
        data class Details(val lessonId: String) : Lessons()
        
        // Lesson section screen
        @Serializable
        data class Section(val lessonId: String, val sectionId: String) : Lessons()
    }
}