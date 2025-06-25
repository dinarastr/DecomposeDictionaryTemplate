package ru.dinarastepina.ulchidictionary.audio

actual class AudioPlayerFactory {
    actual fun createAudioPlayer(): AudioPlayer {
        return AndroidAudioPlayer()
    }
} 