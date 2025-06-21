package com.dinarastepina.decomposedictionary.presentation

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.dinarastepina.decomposedictionary.presentation.components.root.RootComponent
import com.dinarastepina.decomposedictionary.presentation.ui.main.MainScreen

@Composable
fun App(rootComponent: RootComponent) {
    RootContent(rootComponent)
}

@Composable
private fun RootContent(component: RootComponent) {
    Children(
        stack = component.stack,
        animation = stackAnimation(fade().plus(scale())),
    ) { child ->
        when (val instance = child.instance) {
            is RootComponent.Child.Main -> MainScreen(instance.component)
        }
    }
}