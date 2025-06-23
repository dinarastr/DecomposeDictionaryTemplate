package com.dinarastepina.decomposedictionary.data.mapper

import com.dinarastepina.decomposedictionary.data.local.entity.WordEntity
import com.dinarastepina.decomposedictionary.domain.model.Difficulty
import com.dinarastepina.decomposedictionary.domain.model.Word

/**
 * Maps WordEntity (database model) to Word (domain model).
 * Combines information from all translations while preserving semantic separation.
 */
fun WordEntity.toDomain(): Word {
    return Word(
        id = id.toString(),
        text = word,
        definition = extractDefinition(),
        pronunciation = null, // No longer available since udar was removed
        partOfSpeech = extractPartOfSpeech(),
        examples = extractExamples(),
        synonyms = emptyList(), // Not available in current schema
        antonyms = emptyList(), // Not available in current schema
        difficulty = Difficulty.BEGINNER, // Default difficulty
        isFavorite = false // Default value
    )
}

/**
 * Extracts the main definition from all translations.
 * Groups definitions by translation to preserve semantic separation.
 */
private fun WordEntity.extractDefinition(): String {
    if (translations.isEmpty()) return "No definition available"
    
    val translationDefinitions = translations.mapIndexedNotNull { index, translation ->
        val definitions = translation.definition
        if (definitions.isNotEmpty()) {
            val prefix = if (translations.size > 1) "${index + 1}. " else ""
            val translationText = translation.text?.let { " ($it)" } ?: ""
            val definitionTexts = definitions.joinToString("; ") { def ->
                if (def.com != null) {
                    "${def.com}: ${def.text}"
                } else {
                    def.text
                }
            }
            "$prefix$definitionTexts$translationText"
        } else null
    }
    
    return if (translationDefinitions.isNotEmpty()) {
        translationDefinitions.joinToString(" | ")
    } else {
        "No definition available"
    }
}

/**
 * Extracts part of speech from acronyms across all translations.
 */
private fun WordEntity.extractPartOfSpeech(): String? {
    translations.forEach { translation ->
        val acronyms = translation.acronym
        val partOfSpeech = acronyms.firstOrNull { acronym ->
            acronym.title.contains("род", ignoreCase = true) ||
            acronym.title.contains("наречие", ignoreCase = true) ||
            acronym.title.contains("глагол", ignoreCase = true) ||
            acronym.title.contains("прилагательное", ignoreCase = true)
        }?.text
        if (partOfSpeech != null) return partOfSpeech
    }
    return null
}

/**
 * Extracts examples from all translations.
 */
private fun WordEntity.extractExamples(): List<String> {
    return translations.flatMap { translation ->
        translation.example.map { example ->
            "${example.ex} → ${example.exTr}"
        }
    }
}

/**
 * Maps a list of WordEntity to a list of Word.
 */
fun List<WordEntity>.toDomain(): List<Word> {
    return map { it.toDomain() }
} 