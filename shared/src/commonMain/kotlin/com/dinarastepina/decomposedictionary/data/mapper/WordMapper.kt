package com.dinarastepina.decomposedictionary.data.mapper

import com.dinarastepina.decomposedictionary.data.local.entity.WordEntity
import com.dinarastepina.decomposedictionary.data.local.entity.Translations
import com.dinarastepina.decomposedictionary.domain.model.Translation
import com.dinarastepina.decomposedictionary.domain.model.Word

/**
 * Maps WordEntity (database model) to Word (domain model).
 * Preserves semantic separation by creating distinct Translation objects.
 */
fun WordEntity.toDomain(): Word {
    return Word(
        id = id.toString(),
        text = word,
        translations = translations.mapIndexed { index, translation ->
            translation.toDomain(index + 1, translations.size > 1)
        }
    )
}

/**
 * Maps Translations entity to Translation domain model.
 */
private fun Translations.toDomain(index: Int, hasMultiple: Boolean): Translation {
    val definitionText = if (definition.isNotEmpty()) {
        definition.joinToString("; ") { def ->
            if (def.com != null) {
                "${def.com}: ${def.text}"
            } else {
                def.text
            }
        }
    } else {
        "No definition available"
    }
    
    val examples = example.map { ex ->
        "${ex.ex} → ${ex.exTr}"
    }
    
    val partOfSpeech = acronym.firstOrNull { acronym ->
        acronym.title.contains("род", ignoreCase = true) ||
        acronym.title.contains("наречие", ignoreCase = true) ||
        acronym.title.contains("глагол", ignoreCase = true) ||
        acronym.title.contains("прилагательное", ignoreCase = true)
    }?.text
    
    return Translation(
        definition = definitionText,
        partOfSpeech = partOfSpeech,
        examples = examples,
        comment = com,
        number = if (hasMultiple) text ?: "$index." else null
    )
}

/**
 * Maps a list of WordEntity to a list of Word.
 */
fun List<WordEntity>.toDomain(): List<Word> {
    return map { it.toDomain() }
} 