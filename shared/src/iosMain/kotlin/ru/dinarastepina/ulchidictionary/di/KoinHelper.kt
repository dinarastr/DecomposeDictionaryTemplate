package ru.dinarastepina.ulchidictionary.di

import org.koin.core.context.startKoin

fun initKoin() = startKoin {
    modules(
        platformModule(),
        dataModule,
        audioModule,
        presentationModule
    )
}