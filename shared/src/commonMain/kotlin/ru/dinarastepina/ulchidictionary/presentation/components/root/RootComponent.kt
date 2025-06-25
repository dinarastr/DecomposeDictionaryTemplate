package ru.dinarastepina.ulchidictionary.presentation.components.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import ru.dinarastepina.ulchidictionary.presentation.components.main.MainComponent
import ru.dinarastepina.ulchidictionary.presentation.navigation.RootConfig

interface RootComponent {
    val stack: Value<ChildStack<RootConfig, Child>>
    
    sealed class Child {
        data class Main(val component: MainComponent) : Child()
    }
    
    fun interface Factory {
        operator fun invoke(componentContext: ComponentContext): RootComponent
    }
}