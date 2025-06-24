package com.dinarastepina.decomposedictionary.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Topic(
    val id: Int = 0,
    val ulchi: String,
    val russian: String,
    val picture: String
)