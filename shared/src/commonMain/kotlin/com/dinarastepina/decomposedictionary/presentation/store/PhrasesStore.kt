package com.dinarastepina.decomposedictionary.presentation.store


import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.dinarastepina.decomposedictionary.audio.AudioTrack
import com.dinarastepina.decomposedictionary.audio.PlaylistManager
import com.dinarastepina.decomposedictionary.domain.model.Phrase
import com.dinarastepina.decomposedictionary.domain.model.Topic
import com.dinarastepina.decomposedictionary.domain.repository.PhraseBookRepository
import kotlinx.coroutines.launch

interface PhrasesStore : Store<PhrasesStore.Intent, PhrasesStore.State, PhrasesStore.Label> {

    sealed class Intent {
        data object LoadPhrases : Intent()
        data class PlayAudio(val phrase: Phrase) : Intent()
        data object PauseAudio : Intent()
        data object StopAudio : Intent()
        data object Release : Intent()
    }

    data class State(
        val topic: Topic,
        val phrases: List<Phrase> = emptyList(),
        val currentlyPlayingPhrase: Phrase? = null,
        val isPlaying: Boolean = false,
        val isLoading: Boolean = false,
        val error: String? = null
    )

    sealed class Label {
        data object NavigateBack : Label()
    }
}

class PhrasesStoreFactory(
    private val storeFactory: StoreFactory,
    private val phraseBookRepository: PhraseBookRepository,
    private val playlistManager: PlaylistManager
) {
    fun create(topic: Topic): PhrasesStore =
        object : PhrasesStore, Store<PhrasesStore.Intent, PhrasesStore.State, PhrasesStore.Label> by storeFactory.create(
            name = "PhrasesStore",
            initialState = PhrasesStore.State(topic = topic),
            bootstrapper = BootstrapperImpl(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed class Action {
        data object LoadPhrases : Action()
    }

    private sealed class Message {
        data object LoadingStarted : Message()
        data class PhrasesLoaded(val phrases: List<Phrase>) : Message()
        data class ErrorOccurred(val error: String) : Message()
        data class AudioStarted(val phrase: Phrase) : Message()
        data object AudioPaused : Message()
        data object AudioStopped : Message()
        data object AudioCompleted : Message()
    }

    private class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            dispatch(Action.LoadPhrases)
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<PhrasesStore.Intent, Action, PhrasesStore.State, Message, PhrasesStore.Label>() {
        
        init {
            playlistManager.setOnTrackCompletedListener {
                dispatch(Message.AudioCompleted)
            }
        }

        override fun executeIntent(intent: PhrasesStore.Intent) {
            when (intent) {
                is PhrasesStore.Intent.LoadPhrases -> executeAction(Action.LoadPhrases)
                is PhrasesStore.Intent.PlayAudio -> playAudio(intent.phrase)
                is PhrasesStore.Intent.PauseAudio -> pauseAudio()
                is PhrasesStore.Intent.StopAudio -> stopAudio()
                is PhrasesStore.Intent.Release -> release()
            }
        }

        override fun executeAction(action: Action) {
            when (action) {
                is Action.LoadPhrases -> loadPhrases()
            }
        }

        private fun loadPhrases() {
            scope.launch {
                try {
                    dispatch(Message.LoadingStarted)
                    val phrases = phraseBookRepository.getPhrasesByTopic(state().topic.id)
                    dispatch(Message.PhrasesLoaded(phrases))
                } catch (e: Exception) {
                    dispatch(Message.ErrorOccurred(e.message ?: "Failed to load phrases"))
                }
            }
        }

        private fun playAudio(phrase: Phrase) {
            scope.launch {
                try {
                    val currentState = state()
                    if (currentState.currentlyPlayingPhrase?.id == phrase.id && currentState.isPlaying) {
                        pauseAudio()
                        return@launch
                    }
                    
                    if (currentState.currentlyPlayingPhrase?.id == phrase.id && !currentState.isPlaying) {
                        playlistManager.playCurrentTrack()
                        dispatch(Message.AudioStarted(phrase))
                        return@launch
                    }
                    
                    val audioTrack = AudioTrack(
                        id = phrase.id,
                        path = "${phrase.audio}.mp3",
                        title = phrase.ulchi,
                        artist = "Ulchi Dictionary"
                    )
                    
                    playlistManager.playTrack(audioTrack)
                    dispatch(Message.AudioStarted(phrase))
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

    private object ReducerImpl : Reducer<PhrasesStore.State, Message> {
        override fun PhrasesStore.State.reduce(message: Message): PhrasesStore.State =
            when (message) {
                is Message.LoadingStarted -> copy(
                    isLoading = true,
                    error = null
                )
                is Message.PhrasesLoaded -> copy(
                    phrases = message.phrases,
                    isLoading = false,
                    error = null
                )
                is Message.ErrorOccurred -> copy(
                    isLoading = false,
                    error = message.error
                )
                is Message.AudioStarted -> copy(
                    currentlyPlayingPhrase = message.phrase,
                    isPlaying = true,
                    error = null
                )
                is Message.AudioPaused -> copy(
                    isPlaying = false
                )
                is Message.AudioStopped -> copy(
                    currentlyPlayingPhrase = null,
                    isPlaying = false
                )
                is Message.AudioCompleted -> copy(
                    currentlyPlayingPhrase = null,
                    isPlaying = false
                )
            }
    }
} 