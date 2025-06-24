package com.dinarastepina.decomposedictionary.presentation.ui.phrasebook.search

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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.collectAsLazyPagingItems
import app.cash.paging.compose.itemKey
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.dinarastepina.decomposedictionary.presentation.components.phrasebook.search.SearchComponent
import com.dinarastepina.decomposedictionary.presentation.ui.kit.AudioState
import com.dinarastepina.decomposedictionary.presentation.ui.kit.PhraseCard
import com.dinarastepina.decomposedictionary.utils.hideKeyboardOnTap
import decomposedictionary.shared.generated.resources.Res
import decomposedictionary.shared.generated.resources.ic_arrow
import decomposedictionary.shared.generated.resources.ic_back
import decomposedictionary.shared.generated.resources.ic_clear
import org.jetbrains.compose.resources.painterResource

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
                        text = "Search Phrases",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = component::onBackClick) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_back),
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search TextField
            OutlinedTextField(
                value = state.query,
                onValueChange = component::onSearchQuery,
                label = { Text("Search phrases...") },
                placeholder = { Text("Enter Ulchi or Russian text") },
                trailingIcon = {
                    if (state.query.isNotEmpty()) {
                        IconButton(onClick = component::onClearSearch) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_clear),
                                contentDescription = "Clear search"
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                singleLine = true
            )

            // Error handling
            if (state.error != null) {
                Box(
                    modifier = Modifier.fillMaxSize().hideKeyboardOnTap(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Search Error",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = state.error.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            } else {
                // Phrases list with paging
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