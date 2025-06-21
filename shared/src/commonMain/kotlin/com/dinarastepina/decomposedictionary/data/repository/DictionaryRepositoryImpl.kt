package com.dinarastepina.decomposedictionary.data.repository

import com.dinarastepina.decomposedictionary.domain.model.Difficulty
import com.dinarastepina.decomposedictionary.domain.model.Word
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * Implementation of DictionaryRepository with mock data.
 * In a real app, this would use Room database.
 */
class DictionaryRepositoryImpl : DictionaryRepository {
    
    private val _words = MutableStateFlow(createMockWords())
    
    override fun getAllWords(): Flow<List<Word>> = _words
    
    override fun searchWords(query: String): Flow<List<Word>> {
        return _words.map { words ->
            if (query.isBlank()) {
                emptyList()
            } else {
                words.filter { word ->
                    word.text.contains(query, ignoreCase = true) ||
                    word.definition.contains(query, ignoreCase = true)
                }
            }
        }
    }
    
    private fun createMockWords(): List<Word> = listOf(
        Word(
            id = "1",
            text = "Hello",
            definition = "A greeting used when meeting someone",
            pronunciation = "/həˈloʊ/",
            partOfSpeech = "interjection",
            examples = listOf("Hello, how are you?", "She said hello to everyone"),
            difficulty = Difficulty.BEGINNER
        ),
        Word(
            id = "2",
            text = "Dictionary",
            definition = "A book or electronic resource that lists words in alphabetical order and gives their meaning",
            pronunciation = "/ˈdɪkʃəˌnɛri/",
            partOfSpeech = "noun",
            examples = listOf("Look it up in the dictionary", "This is a comprehensive dictionary"),
            difficulty = Difficulty.INTERMEDIATE
        ),
        Word(
            id = "3",
            text = "Serendipity",
            definition = "The occurrence and development of events by chance in a happy or beneficial way",
            pronunciation = "/ˌsɛrənˈdɪpɪti/",
            partOfSpeech = "noun",
            examples = listOf("It was pure serendipity that we met", "A serendipitous discovery"),
            difficulty = Difficulty.ADVANCED
        ),
        Word(
            id = "4",
            text = "Learn",
            definition = "To acquire knowledge or skill by study, experience, or being taught",
            pronunciation = "/lɜrn/",
            partOfSpeech = "verb",
            examples = listOf("I want to learn Spanish", "She learns quickly"),
            difficulty = Difficulty.BEGINNER
        ),
        Word(
            id = "5",
            text = "Beautiful",
            definition = "Pleasing the senses or mind aesthetically",
            pronunciation = "/ˈbjutəfəl/",
            partOfSpeech = "adjective",
            examples = listOf("What a beautiful sunset", "She has beautiful eyes"),
            difficulty = Difficulty.BEGINNER
        )
    )
} 