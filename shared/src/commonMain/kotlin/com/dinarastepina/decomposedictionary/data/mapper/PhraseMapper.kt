package com.dinarastepina.decomposedictionary.data.mapper

import com.dinarastepina.decomposedictionary.data.local.entity.PhraseEntity
import com.dinarastepina.decomposedictionary.data.local.entity.TopicEntity
import com.dinarastepina.decomposedictionary.domain.model.Phrase
import com.dinarastepina.decomposedictionary.domain.model.Topic

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