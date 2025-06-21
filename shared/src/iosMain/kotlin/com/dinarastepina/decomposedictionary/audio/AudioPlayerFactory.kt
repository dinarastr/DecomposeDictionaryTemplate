package com.dinarastepina.decomposedictionary.audio


actual class AudioPlayerFactory {
    actual fun createAudioPlayer(): AudioPlayer {
        return IosAudioPlayer()
    }
} 