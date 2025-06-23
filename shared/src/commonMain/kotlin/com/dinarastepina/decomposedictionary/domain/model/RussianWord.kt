package com.dinarastepina.decomposedictionary.domain.model

/**
 * Domain model representing a word in the dictionary.
 */
data class RussianWord(
    val id: String,
    val text: String,
    val grammar: String? = null,
    val translations: List<Translation>
)

/**
 * Domain model representing a single translation/meaning of a word.
 */
data class Translation(
    val definition: String,
    val partOfSpeech: String? = null,
    val examples: List<String> = emptyList(),
    val comment: String? = null, // Additional context like "то, что взято взаймы"
    val number: String? = null // Roman numerals like "I.", "II." for different meanings
)