package com.dinarastepina.decomposedictionary.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.dinarastepina.decomposedictionary.presentation.components.LessonDetailsComponent
import com.dinarastepina.decomposedictionary.presentation.components.LessonSectionComponent
import com.dinarastepina.decomposedictionary.presentation.components.LessonsComponent
import com.dinarastepina.decomposedictionary.presentation.components.LessonsListComponent
import com.dinarastepina.decomposedictionary.presentation.components.MainComponent
import com.dinarastepina.decomposedictionary.presentation.components.TopicDetailsComponent
import com.dinarastepina.decomposedictionary.presentation.components.TopicsComponent
import com.dinarastepina.decomposedictionary.presentation.components.TopicsListComponent
import com.dinarastepina.decomposedictionary.presentation.navigation.TabConfig
import decomposedictionary.shared.generated.resources.Res
import decomposedictionary.shared.generated.resources.ic_dictionary
import decomposedictionary.shared.generated.resources.ic_list
import decomposedictionary.shared.generated.resources.ic_school
import org.jetbrains.compose.resources.painterResource

/**
 * Main screen with bottom navigation that displays different tabs.
 */
@Composable
fun MainScreen(component: MainComponent) {
    val activeTab by component.activeTab.subscribeAsState()
    val slot by component.slot.subscribeAsState()
    
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                activeTab = activeTab,
                onTabSelected = component::selectTab
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Manual slot observation as recommended by Decompose docs
            slot.child?.instance?.also { child ->
                when (child) {
                    is MainComponent.Child.Dictionary -> DictionaryScreen(child.component)
                    is MainComponent.Child.Topics -> TopicsScreen(child.component)
                    is MainComponent.Child.Lessons -> LessonsScreen(child.component)
                }
            }
        }
    }
}

/**
 * Bottom navigation bar with tabs for Dictionary, Topics, and Lessons.
 */
@Composable
private fun BottomNavigationBar(
    activeTab: TabConfig,
    onTabSelected: (TabConfig) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(
                painter = painterResource(Res.drawable.ic_dictionary),
                contentDescription = "Dictionary") },
            label = { Text("Dictionary") },
            selected = activeTab is TabConfig.Dictionary,
            onClick = { onTabSelected(TabConfig.Dictionary) }
        )
        
        NavigationBarItem(
            icon = { Icon(
                painter = painterResource(Res.drawable.ic_list),
                contentDescription = "Topics") },
            label = { Text("Topics") },
            selected = activeTab is TabConfig.Topics,
            onClick = { onTabSelected(TabConfig.Topics.List) }
        )
        
        NavigationBarItem(
            icon = { Icon(
                painter = painterResource(Res.drawable.ic_school),
                contentDescription = "Lessons") },
            label = { Text("Lessons") },
            selected = activeTab is TabConfig.Lessons,
            onClick = { onTabSelected(TabConfig.Lessons.List) }
        )
    }
}

/**
 * Screen for the Topics tab with nested navigation.
 */
@Composable
private fun TopicsScreen(component: TopicsComponent) {
    // Render different screens based on the active child in the stack
    Children(
        stack = component.stack,
        animation = stackAnimation(fade()),
    ) { child ->
        when (val instance = child.instance) {
            is TopicsComponent.Child.List -> TopicsListScreen(instance.component)
            is TopicsComponent.Child.Details -> TopicDetailsScreen(instance.component)
        }
    }
}

/**
 * Screen for the topics list.
 */
@Composable
private fun TopicsListScreen(component: TopicsListComponent) {
    // Implement topics list UI
    Text("Topics List Screen")
}

/**
 * Screen for topic details.
 */
@Composable
private fun TopicDetailsScreen(component: TopicDetailsComponent) {
    // Implement topic details UI
    Text("Topic Details Screen")
}

/**
 * Screen for the Lessons tab with nested navigation.
 */
@Composable
private fun LessonsScreen(component: LessonsComponent) {
    // Render different screens based on the active child in the stack
    Children(
        stack = component.stack,
        animation = stackAnimation(fade()),
    ) { child ->
        when (val instance = child.instance) {
            is LessonsComponent.Child.List -> LessonsListScreen(instance.component)
            is LessonsComponent.Child.Details -> LessonDetailsScreen(instance.component)
            is LessonsComponent.Child.Section -> LessonSectionScreen(instance.component)
        }
    }
}

/**
 * Screen for the lessons list.
 */
@Composable
private fun LessonsListScreen(component: LessonsListComponent) {
    // Implement lessons list UI
    Text("Lessons List Screen")
}

/**
 * Screen for lesson details with sections.
 */
@Composable
private fun LessonDetailsScreen(component: LessonDetailsComponent) {
    // Implement lesson details UI
    Text("Lesson Details Screen")
}

/**
 * Screen for a lesson section.
 */
@Composable
private fun LessonSectionScreen(component: LessonSectionComponent) {
    // Implement lesson section UI
    Text("Lesson Section Screen")
}