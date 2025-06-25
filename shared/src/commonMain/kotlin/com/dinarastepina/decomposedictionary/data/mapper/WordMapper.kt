package com.dinarastepina.decomposedictionary.data.mapper

import com.dinarastepina.decomposedictionary.data.local.entity.RussianWordEntity
import com.dinarastepina.decomposedictionary.data.local.entity.Translations
import com.dinarastepina.decomposedictionary.data.local.entity.UlchiTranslation
import com.dinarastepina.decomposedictionary.data.local.entity.UlchiWordEntity
import com.dinarastepina.decomposedictionary.domain.model.Translation
import com.dinarastepina.decomposedictionary.domain.model.Word

fun RussianWordEntity.toDomain(): Word {
    // Extract grammar information from all translations
    val grammarInfo = translations.mapNotNull { translation ->
        translation.grammar?.let { grammar ->
            val acronymText = grammar.acronym?.joinToString(", ") { acronym ->
                acronym.text
            }
            val grammarParts = listOfNotNull(acronymText, grammar.text.takeIf { it.isNotBlank() })
            if (grammarParts.isNotEmpty()) grammarParts.joinToString(" ") else null
        }
    }.firstOrNull()

    return Word(
        id = id.toString(),
        text = word,
        grammar = grammarInfo,
        translations = translations.mapIndexed { index, translation ->
            translation.toDomain(index + 1, translations.size > 1)
        }
    )
}

fun UlchiWordEntity.toDomain(): Word {
    return Word(
        id = id.toString(),
        text = word,
        grammar = grammar,
        translations = translations.mapIndexed { index, translation ->
            translation.toDomain(index + 1, translations.size > 1)
        }
    )
}

private fun UlchiTranslation.toDomain(index: Int, hasMultiple: Boolean): Translation {
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

    val examples = examples.map { ex ->
        "${ex.ex} → ${ex.exTr}"
    }

    return Translation(
        definition = definitionText,
        examples = examples,
        number = if (hasMultiple) text ?: "$index." else null
    )
}

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