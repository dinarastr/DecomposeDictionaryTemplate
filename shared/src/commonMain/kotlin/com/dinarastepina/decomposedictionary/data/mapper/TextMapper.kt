package com.dinarastepina.decomposedictionary.data.mapper

import com.dinarastepina.decomposedictionary.data.local.entity.TextEntity
import com.dinarastepina.decomposedictionary.domain.model.Text

fun TextEntity.toDomain(): Text {
    return Text(
        id = id,
        russian = russian,
        ulchi = ulchi,
        audio = audio
    )
}