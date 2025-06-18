package com.dinarastepina.decomposedictionary.presentation.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.dinarastepina.decomposedictionary.presentation.navigation.TabConfig

/**
 * Lessons component that manages navigation between lessons list, lesson details, and lesson sections.
 */
interface LessonsComponent {
    // Expose the navigation stack as a Value (observable)
    val stack: Value<ChildStack<TabConfig.Lessons, Child>>
    
    // Navigate to lesson details
    fun navigateToLessonDetails(lessonId: String)
    
    // Navigate to lesson section
    fun navigateToLessonSection(lessonId: String, sectionId: String)
    
    // Navigate back
    fun navigateBack()
    
    // Child types that can be in the stack
    sealed class Child {
        // Lessons list screen
        data class List(val component: LessonsListComponent) : Child()
        
        // Lesson details screen
        data class Details(val component: LessonDetailsComponent) : Child()
        
        // Lesson section screen
        data class Section(val component: LessonSectionComponent) : Child()
    }
}

/**
 * Default implementation of the LessonsComponent interface.
 */
class DefaultLessonsComponent(
    componentContext: ComponentContext,
    private val storeFactory: StoreFactory = DefaultStoreFactory(),
) : LessonsComponent, ComponentContext by componentContext {
    
    // Navigation controller for the lessons stack
    private val navigation = StackNavigation<TabConfig.Lessons>()
    
    // Initialize the navigation stack with the Lessons list
    override val stack: Value<ChildStack<TabConfig.Lessons, LessonsComponent.Child>> = childStack(
        source = navigation,
        serializer = TabConfig.Lessons.serializer(),
        initialConfiguration = TabConfig.Lessons.List, // Start with the list
        handleBackButton = true, // Handle system back button
        childFactory = ::createChild,
        key = "LessonsStack"
    )
    
    // Factory method to create child components based on configuration
    private fun createChild(config: TabConfig.Lessons, context: ComponentContext): LessonsComponent.Child =
        when (config) {
            is TabConfig.Lessons.List -> LessonsComponent.Child.List(
                component = DefaultLessonsListComponent(
                    componentContext = context,
                    storeFactory = storeFactory,
                    onLessonSelected = ::navigateToLessonDetails
                )
            )
            is TabConfig.Lessons.Details -> LessonsComponent.Child.Details(
                component = DefaultLessonDetailsComponent(
                    componentContext = context,
                    storeFactory = storeFactory,
                    lessonId = config.lessonId,
                    onSectionSelected = { sectionId -> navigateToLessonSection(config.lessonId, sectionId) }
                )
            )
            is TabConfig.Lessons.Section -> LessonsComponent.Child.Section(
                component = DefaultLessonSectionComponent(
                    componentContext = context,
                    storeFactory = storeFactory,
                    lessonId = config.lessonId,
                    sectionId = config.sectionId
                )
            )
        }
    
    // Navigate to lesson details
    override fun navigateToLessonDetails(lessonId: String) {
        navigation.push(TabConfig.Lessons.Details(lessonId))
    }
    
    // Navigate to lesson section
    override fun navigateToLessonSection(lessonId: String, sectionId: String) {
        navigation.push(TabConfig.Lessons.Section(lessonId, sectionId))
    }
    
    // Navigate back
    override fun navigateBack() {
        navigation.pop()
    }
}

/**
 * Component for displaying the list of lessons.
 */
interface LessonsListComponent {
    // Add methods and properties as needed
}

/**
 * Default implementation of the LessonsListComponent interface.
 */
class DefaultLessonsListComponent(
    componentContext: ComponentContext,
    private val storeFactory: StoreFactory,
    private val onLessonSelected: (String) -> Unit
) : LessonsListComponent, ComponentContext by componentContext {
    // Implement lessons list functionality using MVIKotlin
}

/**
 * Component for displaying lesson details with sections.
 */
interface LessonDetailsComponent {
    // Add methods and properties as needed
}

/**
 * Default implementation of the LessonDetailsComponent interface.
 */
class DefaultLessonDetailsComponent(
    componentContext: ComponentContext,
    private val storeFactory: StoreFactory,
    private val lessonId: String,
    private val onSectionSelected: (String) -> Unit
) : LessonDetailsComponent, ComponentContext by componentContext {
    // Implement lesson details functionality using MVIKotlin
}

/**
 * Component for displaying a lesson section.
 */
interface LessonSectionComponent {
    // Add methods and properties as needed
}

/**
 * Default implementation of the LessonSectionComponent interface.
 */
class DefaultLessonSectionComponent(
    componentContext: ComponentContext,
    private val storeFactory: StoreFactory,
    private val lessonId: String,
    private val sectionId: String
) : LessonSectionComponent, ComponentContext by componentContext {
    // Implement lesson section functionality using MVIKotlin
}