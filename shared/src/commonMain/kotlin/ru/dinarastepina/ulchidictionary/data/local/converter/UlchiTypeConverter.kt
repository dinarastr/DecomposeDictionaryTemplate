package ru.dinarastepina.ulchidictionary.data.local.converter

import kotlinx.serialization.KSerializer
import ru.dinarastepina.ulchidictionary.data.local.entity.UlchiTranslation
import ru.dinarastepina.ulchidictionary.data.local.serializer.UlchiTranslationsListSerializer

class UlchiTypeConverter : BaseTypeConverter<UlchiTranslation>() {

    override val listSerializer: KSerializer<List<UlchiTranslation>> = UlchiTranslationsListSerializer
    override val itemSerializer: KSerializer<UlchiTranslation> = UlchiTranslation.serializer()
}