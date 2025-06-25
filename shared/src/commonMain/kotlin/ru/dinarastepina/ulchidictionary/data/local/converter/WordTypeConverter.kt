package ru.dinarastepina.ulchidictionary.data.local.converter

import kotlinx.serialization.KSerializer
import ru.dinarastepina.ulchidictionary.data.local.entity.Translations
import ru.dinarastepina.ulchidictionary.data.local.serializer.TranslationsListSerializer

class WordTypeConverter : BaseTypeConverter<Translations>() {

    override val listSerializer: KSerializer<List<Translations>> = TranslationsListSerializer
    override val itemSerializer: KSerializer<Translations> = Translations.serializer()
} 