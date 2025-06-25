package ru.dinarastepina.ulchidictionary.data.mapper

import ru.dinarastepina.ulchidictionary.data.local.entity.TextEntity
import ru.dinarastepina.ulchidictionary.domain.model.Text

fun TextEntity.toDomain(): Text {
    return Text(
        id = id,
        russian = russian,
        ulchi = ulchi,
        audio = audio
    )
}