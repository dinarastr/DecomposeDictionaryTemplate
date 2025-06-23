package com.dinarastepina.decomposedictionary.domain.model;

enum class LANGUAGE(val value: String) {
    ULCHI("ульчский"),
    RUSSIAN("русский");

    companion object {
        fun fromString(language: String): LANGUAGE {
            return entries.find { it.value == language } ?: RUSSIAN
        }

        fun targetLanguage(currentLanguage: LANGUAGE): LANGUAGE {
            return if (currentLanguage == ULCHI) RUSSIAN else ULCHI
        }
    }
}