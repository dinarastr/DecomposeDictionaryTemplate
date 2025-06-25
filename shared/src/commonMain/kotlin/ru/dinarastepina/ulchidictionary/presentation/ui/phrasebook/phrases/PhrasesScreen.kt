package ru.dinarastepina.ulchidictionary.presentation.ui.phrasebook.phrases

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.dinarastepina.ulchidictionary.presentation.components.phrasebook.phrases.PhrasesComponent
import ru.dinarastepina.ulchidictionary.presentation.ui.kit.AudioState
import ru.dinarastepina.ulchidictionary.presentation.ui.kit.PhraseCard
import decomposedictionary.shared.generated.resources.Res
import decomposedictionary.shared.generated.resources.ic_back
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhrasesScreen(
    component: PhrasesComponent
) {
    val state by component.state.subscribeAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.primary,
        topBar = {
            TopAppBar(
                title = {
                    Text(state.topic.ulchi)
                },
                navigationIcon = {
                    IconButton(onClick = component::onBackClick) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_back),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(state.phrases, key = { it.id }) { phrase ->
                val audioState = when {
                    state.currentlyPlayingPhrase?.id == phrase.id && state.isPlaying -> AudioState.PLAYING
                    state.currentlyPlayingPhrase?.id == phrase.id && !state.isPlaying -> AudioState.PAUSED
                    else -> AudioState.STOPPED
                }
                
                PhraseCard(
                    isPlaying = audioState,
                    originalText = phrase.ulchi,
                    translation = phrase.russian,
                    onPlayAudio = { component.onPlayAudio(phrase) }
                )
            }
        }
    }
} 