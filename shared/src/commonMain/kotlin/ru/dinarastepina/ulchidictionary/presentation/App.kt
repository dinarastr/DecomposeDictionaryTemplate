package ru.dinarastepina.ulchidictionary.presentation

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import ru.dinarastepina.ulchidictionary.presentation.components.root.RootComponent
import ru.dinarastepina.ulchidictionary.presentation.ui.main.MainScreen
import ru.dinarastepina.ulchidictionary.presentation.ui.ui.AppTheme

@Composable
fun App(rootComponent: RootComponent) {
    AppTheme {
        RootContent(rootComponent)
    }
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