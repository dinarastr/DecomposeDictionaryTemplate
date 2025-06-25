package com.dinarastepina.decomposedictionary.presentation.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.dinarastepina.decomposedictionary.audio.AudioTrack
import com.dinarastepina.decomposedictionary.audio.PlaylistManager
import com.dinarastepina.decomposedictionary.domain.model.Text
import com.dinarastepina.decomposedictionary.presentation.components.texts.TextDetailsComponent.SentencePair
import kotlinx.coroutines.launch

interface TextDetailsStore : Store<TextDetailsStore.Intent, TextDetailsStore.State, TextDetailsStore.Label> {

    sealed class Intent {
        data object LoadSentences : Intent()
        data object PlayAudio : Intent()
        data object PauseAudio : Intent()
        data object StopAudio : Intent()
        data object Release : Intent()
    }

    data class State(
        val text: Text,
        val sentencePairs: List<SentencePair> = emptyList(),
        val currentlyPlayingText: Text? = null,
        val isPlaying: Boolean = false,
        val isLoading: Boolean = false,
        val error: String? = null
    )

    sealed class Label {
        data object NavigateBack : Label()
    }
}

class TextDetailsStoreFactory(
    private val storeFactory: StoreFactory,
    private val playlistManager: PlaylistManager
) {
    fun create(text: Text): TextDetailsStore =
        object : TextDetailsStore, Store<TextDetailsStore.Intent, TextDetailsStore.State, TextDetailsStore.Label> by storeFactory.create(
            name = "TextDetailsStore",
            initialState = TextDetailsStore.State(text = text),
            bootstrapper = BootstrapperImpl(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed class Action {
        data object LoadSentences : Action()
    }

    private sealed class Message {
        data object LoadingStarted : Message()
        data class SentencesLoaded(val sentencePairs: List<SentencePair>) : Message()
        data class ErrorOccurred(val error: String) : Message()
        
        data class AudioStarted(val text: Text) : Message()
        data object AudioPaused : Message()
        data object AudioStopped : Message()
        data object AudioCompleted : Message()
    }

    private class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            dispatch(Action.LoadSentences)
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<TextDetailsStore.Intent, Action, TextDetailsStore.State, Message, TextDetailsStore.Label>() {
        
        init {
            playlistManager.setOnTrackCompletedListener {
                dispatch(Message.AudioCompleted)
            }
        }

        override fun executeIntent(intent: TextDetailsStore.Intent) {
            when (intent) {
                is TextDetailsStore.Intent.LoadSentences -> loadSentences()
                is TextDetailsStore.Intent.PlayAudio -> playAudio()
                is TextDetailsStore.Intent.PauseAudio -> pauseAudio()
                is TextDetailsStore.Intent.StopAudio -> stopAudio()
                is TextDetailsStore.Intent.Release -> release()
            }
        }

        override fun executeAction(action: Action) {
            when (action) {
                is Action.LoadSentences -> loadSentences()
            }
        }

        private fun loadSentences() {
            scope.launch {
                try {
                    dispatch(Message.LoadingStarted)
                    val currentState = state()
                    val text = currentState.text
                    
                    // Debug: Print the text object to see the audio field
                    println("Loading text with audio field: '${text.audio}'")
                    
                    val sentencePairs = splitTextIntoSentences(text)
                    dispatch(Message.SentencesLoaded(sentencePairs))
                } catch (e: Exception) {
                    dispatch(Message.ErrorOccurred(e.message ?: "Failed to load sentences"))
                }
            }
        }

        private fun splitTextIntoSentences(text: Text): List<SentencePair> {
            val ulchiSentences = text.ulchi.split(Regex("(?<=[.!?])\\s+")).map { it.trim() }.filter { it.isNotEmpty() }
            val russianSentences = text.russian.split(Regex("(?<=[.!?])\\s+")).map { it.trim() }.filter { it.isNotEmpty() }
            
            val minCount = minOf(ulchiSentences.size, russianSentences.size)
            
            return (0 until minCount).map { index ->
                SentencePair(
                    index = index,
                    ulchi = ulchiSentences[index],
                    russian = russianSentences[index]
                )
            }
        }

        private fun playAudio() {
            scope.launch {
                try {
                    val currentState = state()
                    val text = currentState.text
                    
                    if (currentState.currentlyPlayingText?.id == text.id && currentState.isPlaying) {
                        pauseAudio()
                        return@launch
                    }
                    
                    if (currentState.currentlyPlayingText?.id == text.id && !currentState.isPlaying) {
                        playlistManager.playCurrentTrack()
                        dispatch(Message.AudioStarted(text))
                        return@launch
                    }
                    
                    if (text.audio.isBlank()) {
                        dispatch(Message.ErrorOccurred("No audio file available for this text"))
                        return@launch
                    }
                    
                    val audioTrack = AudioTrack(
                        id = text.id,
                        path = "${text.audio}.mp3",
                        title = "Text #${text.id}",
                        artist = "Ulchi Text"
                    )
                    
                    playlistManager.playTrack(audioTrack)
                    dispatch(Message.AudioStarted(text))
                } catch (e: Exception) {
                    dispatch(Message.ErrorOccurred("Failed to play audio: ${e.message}"))
                }
            }
        }

        private fun pauseAudio() {
            playlistManager.pause()
            dispatch(Message.AudioPaused)
        }

        private fun stopAudio() {
            playlistManager.stop()
            dispatch(Message.AudioStopped)
        }

        private fun release() {
            playlistManager.stop()
            playlistManager.release()
            dispatch(Message.AudioStopped)
        }
    }

    private object ReducerImpl : Reducer<TextDetailsStore.State, Message> {
        override fun TextDetailsStore.State.reduce(message: Message): TextDetailsStore.State =
            when (message) {
                is Message.LoadingStarted -> copy(
                    isLoading = true,
                    error = null
                )
                is Message.SentencesLoaded -> copy(
                    sentencePairs = message.sentencePairs,
                    isLoading = false,
                    error = null
                )
                is Message.ErrorOccurred -> copy(
                    isLoading = false,
                    error = message.error
                )
                is Message.AudioStarted -> copy(
                    currentlyPlayingText = message.text,
                    isPlaying = true,
                    error = null
                )
                is Message.AudioPaused -> copy(
                    isPlaying = false
                )
                is Message.AudioStopped -> copy(
                    currentlyPlayingText = null,
                    isPlaying = false
                )
                is Message.AudioCompleted -> copy(
                    currentlyPlayingText = null,
                    isPlaying = false
                )
            }
    }
} 