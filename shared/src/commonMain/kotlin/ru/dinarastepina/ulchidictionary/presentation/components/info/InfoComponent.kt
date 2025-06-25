package ru.dinarastepina.ulchidictionary.presentation.components.info

import com.arkivanov.decompose.ComponentContext

interface InfoComponent {

    fun interface Factory {
        operator fun invoke(componentContext: ComponentContext): InfoComponent
    }
} 