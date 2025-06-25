package ru.dinarastepina.ulchidictionary.audio

interface AudioCompletionListener {
    fun onAudioCompleted()
}

interface AudioPlayer {
    fun setCompletionListener(listener: AudioCompletionListener)
    fun play()
    fun pause()
    fun stop()
    fun release()
    fun setDataSource(path: String)
    fun isPlaying(): Boolean
} 