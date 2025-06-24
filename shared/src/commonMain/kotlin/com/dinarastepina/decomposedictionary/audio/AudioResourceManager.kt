package com.dinarastepina.decomposedictionary.audio

import decomposedictionary.shared.generated.resources.Res

class AudioResourceManager {
    suspend fun getAudioBytes(path: String): ByteArray {
        return Res.readBytes("files/sounds/$path")
    }
}