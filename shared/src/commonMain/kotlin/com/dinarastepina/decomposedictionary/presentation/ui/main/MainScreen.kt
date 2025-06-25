package com.dinarastepina.decomposedictionary.presentation.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
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
import com.dinarastepina.decomposedictionary.presentation.components.texts.TextsComponent
import com.dinarastepina.decomposedictionary.presentation.navigation.TabConfig
import com.dinarastepina.decomposedictionary.presentation.ui.dictionary.DictionaryScreen
import com.dinarastepina.decomposedictionary.presentation.ui.info.InfoScreen
import com.dinarastepina.decomposedictionary.presentation.ui.phrasebook.phrases.PhrasesScreen
import com.dinarastepina.decomposedictionary.presentation.ui.phrasebook.search.SearchScreen
import com.dinarastepina.decomposedictionary.presentation.ui.phrasebook.topics.TopicsListScreen
import com.dinarastepina.decomposedictionary.presentation.ui.texts.TextDetailsScreen
import com.dinarastepina.decomposedictionary.presentation.ui.texts.TextsListScreen
import decomposedictionary.shared.generated.resources.Res
import decomposedictionary.shared.generated.resources.ic_dictionary
import decomposedictionary.shared.generated.resources.ic_list
import decomposedictionary.shared.generated.resources.ic_communication
import decomposedictionary.shared.generated.resources.ic_info
import decomposedictionary.shared.generated.resources.tab_dictionary
import decomposedictionary.shared.generated.resources.tab_phrases
import decomposedictionary.shared.generated.resources.tab_texts
import decomposedictionary.shared.generated.resources.tab_info
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

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
                    is MainComponent.Child.Texts -> TextsScreen(child.component)
                    is MainComponent.Child.Info -> InfoScreen(child.component)
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
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                selectedIconColor = MaterialTheme.colorScheme.onSecondary
            ),
            icon = { Icon(
                painter = painterResource(Res.drawable.ic_dictionary),
                contentDescription = stringResource(Res.string.tab_dictionary)) },
            label = { Text(stringResource(Res.string.tab_dictionary)) },
            selected = activeTab is TabConfig.Dictionary,
            onClick = { onTabSelected(TabConfig.Dictionary) }
        )
        
        NavigationBarItem(
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                selectedIconColor = MaterialTheme.colorScheme.onSecondary
            ),
            icon = { Icon(
                painter = painterResource(Res.drawable.ic_list),
                contentDescription = stringResource(Res.string.tab_phrases)) },
            label = { Text(stringResource(Res.string.tab_phrases)) },
            selected = activeTab is TabConfig.Topics,
            onClick = { onTabSelected(TabConfig.Topics.List) }
        )
        
        NavigationBarItem(
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                selectedIconColor = MaterialTheme.colorScheme.onSecondary
            ),
            icon = { Icon(
                painter = painterResource(Res.drawable.ic_communication),
                contentDescription = stringResource(Res.string.tab_texts)) },
            label = { Text(stringResource(Res.string.tab_texts)) },
            selected = activeTab is TabConfig.Texts,
            onClick = { onTabSelected(TabConfig.Texts.List) }
        )
        
        NavigationBarItem(
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                selectedIconColor = MaterialTheme.colorScheme.onSecondary
            ),
            icon = { Icon(
                painter = painterResource(Res.drawable.ic_info),
                contentDescription = stringResource(Res.string.tab_info)) },
            label = { Text(stringResource(Res.string.tab_info)) },
            selected = activeTab is TabConfig.Info,
            onClick = { onTabSelected(TabConfig.Info) }
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
            is TopicsComponent.Child.Search -> SearchScreen(instance.component)
        }
    }
}

@Composable
private fun TextsScreen(component: TextsComponent) {
    Children(
        stack = component.stack,
        animation = stackAnimation(fade()),
    ) { child ->
        when (val instance = child.instance) {
            is TextsComponent.Child.List -> TextsListScreen(instance.component)
            is TextsComponent.Child.Details -> TextDetailsScreen(instance.component)
        }
    }
}