package com.dinarastepina.decomposedictionary.audio

import com.dinarastepina.decomposedictionary.data.local.toNSData
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.runBlocking
import platform.AVFAudio.AVAudioPlayer
import platform.AVFAudio.AVAudioPlayerDelegateProtocol
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.setActive
import platform.darwin.NSObject
import platform.Foundation.NSError

@OptIn(ExperimentalForeignApi::class)
class IosAudioPlayer : AudioPlayer {
    private var audioPlayer: AVAudioPlayer? = null
    private var currentDataSource: ByteArray? = null
    private var completionListener: AudioCompletionListener? = null

    private val delegate = object : NSObject(), AVAudioPlayerDelegateProtocol {
        override fun audioPlayerDidFinishPlaying(player: AVAudioPlayer, successfully: Boolean) {
            if (successfully) {
                completionListener?.onAudioCompleted()
            }
        }

        override fun audioPlayerDecodeErrorDidOccur(player: AVAudioPlayer, error: NSError?) {
            error?.let {
                println("Audio decode error: ${it.localizedDescription}")
            }
        }
    }

    init {
        configureAudioSession()
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun configureAudioSession() {
        try {
            AVAudioSession.sharedInstance().setCategory(AVAudioSessionCategoryPlayback, error = null)
            AVAudioSession.sharedInstance().setActive(true, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun setCompletionListener(listener: AudioCompletionListener) {
        completionListener = listener
    }

    override fun play() {
        audioPlayer?.play()
    }

    override fun pause() {
        audioPlayer?.pause()
    }

    override fun stop() {
        audioPlayer?.stop()
        audioPlayer?.currentTime = 0.0
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun release() {
        audioPlayer = null
        currentDataSource = null
        try {
            AVAudioSession.sharedInstance().setActive(false, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun setDataSource(path: String) {
        val audioBytes = runBlocking {
            AudioResourceManager().getAudioBytes(path)
        }
        currentDataSource = audioBytes

        val nsData = audioBytes.toNSData()
        audioPlayer = AVAudioPlayer(nsData!!, error = null)
        audioPlayer?.delegate = delegate
        audioPlayer?.prepareToPlay()
    }

    override fun isPlaying(): Boolean {
        return audioPlayer?.playing ?: false
    }
}