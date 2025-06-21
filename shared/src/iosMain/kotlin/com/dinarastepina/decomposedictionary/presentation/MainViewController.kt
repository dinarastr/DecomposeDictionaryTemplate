package com.dinarastepina.decomposedictionary.presentation

import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.ApplicationLifecycle
import com.dinarastepina.decomposedictionary.di.initKoin
import com.dinarastepina.decomposedictionary.presentation.components.root.RootComponent
import org.koin.mp.KoinPlatform.getKoin

fun MainViewController() = ComposeUIViewController {
    try {
        getKoin()
    } catch (e: Exception) {
        initKoin()
    }
    
    val rootComponent = remember {
        val rootComponentFactory: RootComponent.Factory = getKoin().get()
        rootComponentFactory(DefaultComponentContext(ApplicationLifecycle()))
    }
    App(rootComponent = rootComponent)
}