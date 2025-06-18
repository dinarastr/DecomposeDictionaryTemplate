package com.dinarastepina.decomposedictionary.presentation

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.dinarastepina.decomposedictionary.presentation.components.RootComponent
import com.dinarastepina.decomposedictionary.presentation.ui.MainScreen

@Composable
fun App(rootComponent: RootComponent) {
    // Render the UI based on the root component's state
    RootContent(rootComponent)
}

/**
 * Renders the content of the root component.
 */
@Composable
private fun RootContent(component: RootComponent) {
    // Render different screens based on the active child in the stack
    Children(
        stack = component.stack,
        animation = stackAnimation(fade().plus(scale())),
    ) { child ->
        when (val instance = child.instance) {
            is RootComponent.Child.Main -> MainScreen(instance.component)
        }
    }
}
