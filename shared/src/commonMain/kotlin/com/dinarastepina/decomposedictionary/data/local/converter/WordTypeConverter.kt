package com.dinarastepina.decomposedictionary.data.local.converter

import com.dinarastepina.decomposedictionary.data.local.entity.Translations
import com.dinarastepina.decomposedictionary.data.local.serializer.TranslationsListSerializer
import kotlinx.serialization.KSerializer

class WordTypeConverter : BaseTypeConverter<Translations>() {

    override val listSerializer: KSerializer<List<Translations>> = TranslationsListSerializer
    override val itemSerializer: KSerializer<Translations> = Translations.serializer()
} 