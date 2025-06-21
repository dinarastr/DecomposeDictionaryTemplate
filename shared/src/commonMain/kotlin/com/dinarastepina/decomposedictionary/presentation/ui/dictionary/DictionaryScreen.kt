package com.dinarastepina.decomposedictionary.presentation.ui.dictionary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.dinarastepina.decomposedictionary.presentation.components.dictionary.DictionaryComponent
import com.dinarastepina.decomposedictionary.presentation.store.DictionaryStore
import decomposedictionary.shared.generated.resources.Res
import decomposedictionary.shared.generated.resources.ic_clear
import decomposedictionary.shared.generated.resources.ic_search
import org.jetbrains.compose.resources.painterResource

/**
 * Screen for the Dictionary tab.
 */
@Composable
fun DictionaryScreen(component: DictionaryComponent) {
    val state by component.state.subscribeAsState()
    
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        SearchBar(
            query = state.query,
            onQueryChange = component::search,
            onClearClick = component::clearSearch,
            modifier = Modifier.fillMaxWidth()
        )
        
        // Show initialization status
        if (!state.isInitialized) {
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Initializing dictionary...")
                }
            }
        }
        
        // Popular words section (shown when no search is active)
        if (state.query.isEmpty() && state.popularWords.isNotEmpty()) {
            PopularWordsSection(
                popularWords = state.popularWords,
                onWordClick = {  },
                onRefreshClick = {  }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Main content area
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                state.error != null -> {
                    ErrorSection(
                        error = state.error!!,
                        onRetryClick = { 
                            if (state.query.isNotEmpty()) {
                                component.search(state.query)
                            } else {
                                //component.loadPopularWords()
                            }
                        },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                state.words.isEmpty() && state.query.isNotEmpty() -> {
                    Text(
                        text = "No results found for '${state.query}'",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                state.words.isNotEmpty() -> {
                    WordsList(
                        words = state.words,
                        onWordClick = {

                        }
                    )
                }
                state.query.isEmpty() -> {
                    Text(
                        text = "Search for a word or select from popular words above",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

/**
 * Section showing popular words as chips.
 */
@Composable
private fun PopularWordsSection(
    popularWords: List<DictionaryStore.Word>,
    onWordClick: (DictionaryStore.Word) -> Unit,
    onRefreshClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Popular Words",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = onRefreshClick,
                modifier = Modifier
            ) {
                Text("Refresh")
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(popularWords) { word ->
                FilterChip(
                    selected = false,
                    onClick = { onWordClick(word) },
                    label = { Text(word.text) }
                )
            }
        }
    }
}

/**
 * Error section with retry functionality.
 */
@Composable
private fun ErrorSection(
    error: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Error: $error",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRetryClick) {
            Text("Retry")
        }
    }
}

/**
 * Search bar for the dictionary.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf(query) }
    
    OutlinedTextField(
        value = text,
        onValueChange = { newText ->
            text = newText
            onQueryChange(newText)
        },
        placeholder = { Text("Search for a word...") },
        leadingIcon = { Icon(painter = painterResource(Res.drawable.ic_search), contentDescription = "Search") },
        trailingIcon = {
            if (text.isNotEmpty()) {
                IconButton(onClick = {
                    text = ""
                    onClearClick()
                }) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_clear),
                        contentDescription = "Clear")
                }
            }
        },
        singleLine = true,
        modifier = modifier.padding(bottom = 16.dp)
    )
}

/**
 * List of words in the dictionary.
 */
@Composable
private fun WordsList(
    words: List<DictionaryStore.Word>,
    onWordClick: (DictionaryStore.Word) -> Unit
) {
    LazyColumn {
        items(words) { word ->
            WordCard(
                word = word,
                onClick = { onWordClick(word) }
            )
        }
    }
}

/**
 * Card displaying a word and its definition.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WordCard(
    word: DictionaryStore.Word,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = word.text,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = word.definition,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
            if (word.examples.isNotEmpty()) {
                Text(
                    text = "Examples:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 8.dp)
                )
                word.examples.forEach { example ->
                    Text(
                        text = "• $example",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                    )
                }
            }
        }
    }
}