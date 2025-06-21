package com.dinarastepina.decomposedictionary.data.mapper

import com.dinarastepina.decomposedictionary.data.local.entity.WordEntity
import com.dinarastepina.decomposedictionary.domain.model.Difficulty
import com.dinarastepina.decomposedictionary.domain.model.Word

/**
 * Maps WordEntity (database model) to Word (domain model).
 */
fun WordEntity.toDomain(): Word {
    return Word(
        id = id.toString(),
        text = word,
        definition = extractDefinition(),
        pronunciation = translation.udar,
        partOfSpeech = extractPartOfSpeech(),
        examples = extractExamples(),
        synonyms = emptyList(), // Not available in current schema
        antonyms = emptyList(), // Not available in current schema
        difficulty = Difficulty.BEGINNER, // Default difficulty
        isFavorite = false // Default value
    )
}

/**
 * Extracts the main definition from the translations.
 */
private fun WordEntity.extractDefinition(): String {
    val definitions = translation.definition
    return when {
        definitions.isNotEmpty() -> {
            // Combine all definitions with their comments
            definitions.joinToString("; ") { def ->
                if (def.com != null) {
                    "${def.com}: ${def.text}"
                } else {
                    def.text
                }
            }
        }
        else -> "No definition available"
    }
}

/**
 * Extracts part of speech from acronyms.
 */
private fun WordEntity.extractPartOfSpeech(): String? {
    val acronyms = translation.acronym
    return acronyms.firstOrNull { acronym ->
        acronym.title.contains("род", ignoreCase = true) ||
        acronym.title.contains("наречие", ignoreCase = true) ||
        acronym.title.contains("глагол", ignoreCase = true) ||
        acronym.title.contains("прилагательное", ignoreCase = true)
    }?.text
}

/**
 * Extracts examples from the translations.
 */
private fun WordEntity.extractExamples(): List<String> {
    return translation.example.map { example ->
        "${example.ex} → ${example.exTr}"
    }
}

/**
 * Maps a list of WordEntity to a list of Word.
 */
fun List<WordEntity>.toDomain(): List<Word> {
    return map { it.toDomain() }
} 