package com.dinarastepina.decomposedictionary.data.local.converter

import com.dinarastepina.decomposedictionary.data.local.entity.UlchiTranslation
import com.dinarastepina.decomposedictionary.data.local.serializer.UlchiTranslationsListSerializer
import kotlinx.serialization.KSerializer

class UlchiTypeConverter : BaseTypeConverter<UlchiTranslation>() {

    override val listSerializer: KSerializer<List<UlchiTranslation>> = UlchiTranslationsListSerializer
    override val itemSerializer: KSerializer<UlchiTranslation> = UlchiTranslation.serializer()
}