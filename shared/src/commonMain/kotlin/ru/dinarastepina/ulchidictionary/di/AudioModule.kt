package ru.dinarastepina.ulchidictionary.di

import ru.dinarastepina.ulchidictionary.audio.AudioPlayerFactory
import ru.dinarastepina.ulchidictionary.audio.PlaylistManager
import org.koin.dsl.module

val audioModule = module {
    single { AudioPlayerFactory() }
    single { PlaylistManager(get()) }
}