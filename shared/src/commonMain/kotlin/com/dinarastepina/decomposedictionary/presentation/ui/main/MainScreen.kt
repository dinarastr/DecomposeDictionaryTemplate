package com.dinarastepina.decomposedictionary.presentation.ui.main

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
import com.dinarastepina.decomposedictionary.presentation.components.main.MainComponent
import com.dinarastepina.decomposedictionary.presentation.components.phrasebook.topics.TopicsComponent
import com.dinarastepina.decomposedictionary.presentation.navigation.TabConfig
import com.dinarastepina.decomposedictionary.presentation.ui.dictionary.DictionaryScreen
import com.dinarastepina.decomposedictionary.presentation.ui.phrasebook.phrases.PhrasesScreen
import com.dinarastepina.decomposedictionary.presentation.ui.phrasebook.topics.TopicsListScreen
import decomposedictionary.shared.generated.resources.Res
import decomposedictionary.shared.generated.resources.ic_dictionary
import decomposedictionary.shared.generated.resources.ic_list
import decomposedictionary.shared.generated.resources.ic_school
import org.jetbrains.compose.resources.painterResource

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
            slot.child?.instance?.also { child ->
                when (child) {
                    is MainComponent.Child.Dictionary -> DictionaryScreen(child.component)
                    is MainComponent.Child.Topics -> TopicsScreen(child.component)
                    is MainComponent.Child.Texts -> Text("")
                }
            }
        }
    }
}

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
            selected = activeTab is TabConfig.Texts,
            onClick = { onTabSelected(TabConfig.Texts.List) }
        )
    }
}

@Composable
private fun TopicsScreen(component: TopicsComponent) {
    Children(
        stack = component.stack,
        animation = stackAnimation(fade()),
    ) { child ->
        when (val instance = child.instance) {
            is TopicsComponent.Child.List -> TopicsListScreen(instance.component)
            is TopicsComponent.Child.Details -> PhrasesScreen(instance.component)
            is TopicsComponent.Child.Search -> Text("")
        }
    }
}