package com.dinarastepina.decomposedictionary.di

import com.dinarastepina.decomposedictionary.audio.AudioPlayerFactory
import com.dinarastepina.decomposedictionary.audio.PlaylistManager
import org.koin.dsl.module

val audioModule = module {
    single { AudioPlayerFactory() }
    single { PlaylistManager(get()) }
}