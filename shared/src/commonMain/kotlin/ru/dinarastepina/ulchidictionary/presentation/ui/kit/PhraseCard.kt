package ru.dinarastepina.ulchidictionary.presentation.ui.kit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import decomposedictionary.shared.generated.resources.Res
import org.jetbrains.compose.resources.painterResource
import decomposedictionary.shared.generated.resources.ic_play
import decomposedictionary.shared.generated.resources.ic_pause
import decomposedictionary.shared.generated.resources.ic_play_pause

enum class AudioState {
    PLAYING, PAUSED, STOPPED
}

@Composable
fun PhraseCard(
    originalText: String,
    translation: String,
    onPlayAudio: () -> Unit,
    isPlaying: AudioState = AudioState.STOPPED,
    modifier: Modifier = Modifier
) {

    val audioIcon = when (isPlaying) {
        AudioState.PLAYING -> painterResource(Res.drawable.ic_pause)
        AudioState.PAUSED -> painterResource(Res.drawable.ic_play_pause)
        else -> painterResource(Res.drawable.ic_play)
    }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SelectionContainer {
                    Text(
                        text = originalText,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                SelectionContainer {
                    Text(
                        text = translation,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(
                onClick = onPlayAudio,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    tint = MaterialTheme.colorScheme.primaryContainer,
                    painter = audioIcon,
                    contentDescription = "Play audio"
                )
            }
        }
    }
} 