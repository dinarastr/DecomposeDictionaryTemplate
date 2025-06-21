package com.dinarastepina.decomposedictionary.audio

class PlaylistManager(private val audioPlayerFactory: AudioPlayerFactory) {
    private val player: AudioPlayer = audioPlayerFactory.createAudioPlayer()
    private var onTrackCompleted: (() -> Unit)? = null

    fun setOnTrackCompletedListener(listener: () -> Unit) {
        onTrackCompleted = listener
        player.setCompletionListener(object : AudioCompletionListener {
            override fun onAudioCompleted() {
                onTrackCompleted?.invoke()
            }
        })
    }

    fun playTrack(track: AudioTrack) {
        stop()
        player.setDataSource(track.path)
        player.play()
    }

    fun playCurrentTrack() {
        player.play()
    }

    fun pause() {
        player.pause()
    }

    fun stop() {
        player.stop()
    }

    fun release() {
        player.release()
    }

    fun isPlaying(): Boolean = player.isPlaying()
}