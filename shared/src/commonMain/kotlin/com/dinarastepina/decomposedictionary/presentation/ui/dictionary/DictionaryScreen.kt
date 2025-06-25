package com.dinarastepina.decomposedictionary.presentation.ui.dictionary

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import app.cash.paging.compose.itemKey
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.dinarastepina.decomposedictionary.domain.model.LANGUAGE
import com.dinarastepina.decomposedictionary.domain.model.Translation
import com.dinarastepina.decomposedictionary.domain.model.Word
import com.dinarastepina.decomposedictionary.presentation.ui.kit.LanguageSettingsButton
import com.dinarastepina.decomposedictionary.presentation.components.dictionary.DictionaryComponent
import com.dinarastepina.decomposedictionary.utils.hideKeyboardOnTap
import org.jetbrains.compose.resources.painterResource
import decomposedictionary.shared.generated.resources.Res
import decomposedictionary.shared.generated.resources.ic_search
import decomposedictionary.shared.generated.resources.ic_clear

@Composable
fun DictionaryScreen(component: DictionaryComponent) {
    val state by component.state.subscribeAsState()
    val words = component.wordsPagingFlow.collectAsLazyPagingItems()

    DictionaryContent(
        searchQuery = state.query,
        onSearchQueryChange = component::search,
        onClearSearch = component::clearSearch,
        words = words,
        error = state.error,
        currentLanguage = state.selectedLanguage,
        targetLanguage = state.targetLanguage,
        onLanguageSelected = component::changeLanguage
    )
}

@Composable
private fun DictionaryContent(
    currentLanguage: LANGUAGE,
    targetLanguage: LANGUAGE,
    onLanguageSelected: (LANGUAGE) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    words: LazyPagingItems<Word>,
    error: String?
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SearchBar(
            query = searchQuery,
            onQueryChange = onSearchQueryChange,
            onClearClick = onClearSearch,
            modifier = Modifier.fillMaxWidth()
        )

        LanguageSettingsButton(
            languageOne = currentLanguage,
            languageTwo = targetLanguage,
            onClick = onLanguageSelected
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (error != null) {
            ErrorSection(
                error = error,
                onRetryClick = {
                    if (searchQuery.isNotEmpty()) {
                        onSearchQueryChange(searchQuery)
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally).hideKeyboardOnTap()
            )
            return@Column
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().hideKeyboardOnTap(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                count = words.itemCount,
                key = words.itemKey { it.id }
            ) { index ->
                val word = words[index]
                if (word != null) {
                    WordItem(
                        word = word,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    WordItemPlaceholder()
                }
            }

            if (words.loadState.append.endOfPaginationReached.not()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            if (words.itemCount == 0 && searchQuery.isNotEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp)
                            .hideKeyboardOnTap(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "По запросу '$searchQuery' слов не найдено",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            if (words.itemCount == 0 && searchQuery.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Начните вводить текст для поиска",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Поиск...") },
        leadingIcon = {
            Icon(
                painter = painterResource(Res.drawable.ic_search),
                contentDescription = "Поиск"
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClearClick) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_clear),
                        contentDescription = "Очистить поиск"
                    )
                }
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        singleLine = true
    )
}

@Composable
private fun WordItem(
    word: Word,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = word.text,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            word.grammar?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            word.translations.forEach { translation ->
                Spacer(modifier = Modifier.height(8.dp))
                TranslationItem(translation = translation)
            }
        }
    }
}

@Composable
private fun TranslationItem(
    translation: Translation,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            translation.number?.let { number ->
                Text(
                    text = number,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            translation.partOfSpeech?.let { partOfSpeech ->
                Text(
                    text = partOfSpeech,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        Text(
            text = translation.definition,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )

        translation.comment?.let { comment ->
            Text(
                text = "($comment)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        if (translation.examples.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            translation.examples.take(2).forEach { example ->
                Text(
                    text = "• $example",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 8.dp, top = 1.dp)
                )
            }
        }
    }
}

@Composable
private fun WordItemPlaceholder() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(24.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
            )
        }
    }
}

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
            text = "Ошибка: $error",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRetryClick) {
            Text("Ещё раз")
        }
    }
}