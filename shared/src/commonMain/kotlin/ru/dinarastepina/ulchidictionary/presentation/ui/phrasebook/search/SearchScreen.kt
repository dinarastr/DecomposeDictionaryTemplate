package ru.dinarastepina.ulchidictionary.presentation.ui.phrasebook.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.collectAsLazyPagingItems
import app.cash.paging.compose.itemKey
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.dinarastepina.ulchidictionary.presentation.components.phrasebook.search.SearchComponent
import ru.dinarastepina.ulchidictionary.presentation.ui.kit.AudioState
import ru.dinarastepina.ulchidictionary.presentation.ui.kit.PhraseCard
import ru.dinarastepina.ulchidictionary.utils.hideKeyboardOnTap
import decomposedictionary.shared.generated.resources.Res
import decomposedictionary.shared.generated.resources.ic_back
import decomposedictionary.shared.generated.resources.ic_clear
import decomposedictionary.shared.generated.resources.ic_search
import decomposedictionary.shared.generated.resources.search_phrase_placeholder
import decomposedictionary.shared.generated.resources.error_search
import decomposedictionary.shared.generated.resources.search_phrase
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    component: SearchComponent
) {
    val state by component.state.subscribeAsState()
    val phrases = component.phrasesPagingFlow.collectAsLazyPagingItems()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.primary,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.search_phrase),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = component::onBackClick) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_back),
                            contentDescription = null,
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OutlinedTextField(
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    cursorColor = MaterialTheme.colorScheme.primaryContainer,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                value = state.query,
                onValueChange = component::onSearchQuery,
                placeholder = {
                    Text(stringResource(Res.string.search_phrase_placeholder))
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_search),
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    if (state.query.isNotEmpty()) {
                        IconButton(onClick = component::onClearSearch) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_clear),
                                contentDescription = null
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                singleLine = true
            )

            when {
                state.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(Res.string.error_search),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimary,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().hideKeyboardOnTap(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(
                            count = phrases.itemCount,
                            key = phrases.itemKey { phrase -> phrase.id }
                        ) { index ->
                            val phrase = phrases[index]
                            if (phrase != null) {
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
            }
        }
    }
} 