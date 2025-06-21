package com.dinarastepina.decomposedictionary.audio

data class AudioTrack(
    val id: Int,
    val path: String,
    val title: String,
    val artist: String? = null,
    val duration: Long = 0,
    val isPlaying: Boolean = false
)