package com.dinarastepina.decomposedictionary.data.local.converter

import com.dinarastepina.decomposedictionary.data.local.entity.Translations
import com.dinarastepina.decomposedictionary.data.local.serializer.TranslationsListSerializer
import kotlinx.serialization.KSerializer

/**
 * TypeConverter for Room database to handle JSON serialization/deserialization
 * of Translations objects used in WordEntity with custom serializers for inconsistent schema.
 */
class WordTypeConverter : BaseTypeConverter<Translations>() {

    override val listSerializer: KSerializer<List<Translations>> = TranslationsListSerializer
    override val itemSerializer: KSerializer<Translations> = Translations.serializer()
} 