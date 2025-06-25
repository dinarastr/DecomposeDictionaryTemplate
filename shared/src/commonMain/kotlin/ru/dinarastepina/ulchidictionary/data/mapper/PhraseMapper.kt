package ru.dinarastepina.ulchidictionary.data.mapper

import ru.dinarastepina.ulchidictionary.data.local.entity.PhraseEntity
import ru.dinarastepina.ulchidictionary.data.local.entity.TopicEntity
import ru.dinarastepina.ulchidictionary.domain.model.Phrase
import ru.dinarastepina.ulchidictionary.domain.model.Topic

fun PhraseEntity.toDomain(): Phrase {
    return Phrase(
        id = id,
        russian = russian,
        ulchi = ulchi,
        topicId = topicId,
        audio = audio
    )
}

fun TopicEntity.toDomain(): Topic {
    return Topic(
        id = id,
        ulchi = ulchi,
        russian = russian,
        picture = picture
    )
}