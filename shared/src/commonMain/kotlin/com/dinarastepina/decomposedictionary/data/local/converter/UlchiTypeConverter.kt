package com.dinarastepina.decomposedictionary.data.local.converter

import com.dinarastepina.decomposedictionary.data.local.entity.UlchiTranslation
import com.dinarastepina.decomposedictionary.data.local.entity.UlchiTranslationsListSerializer
import kotlinx.serialization.KSerializer

/**
 * TypeConverter for Room database to handle JSON serialization/deserialization
 * of UlchiTranslation objects with custom serializers for inconsistent schema.
 */
class UlchiTypeConverter : BaseTypeConverter<UlchiTranslation>() {

    override val listSerializer: KSerializer<List<UlchiTranslation>> = UlchiTranslationsListSerializer
    override val itemSerializer: KSerializer<UlchiTranslation> = UlchiTranslation.serializer()
}