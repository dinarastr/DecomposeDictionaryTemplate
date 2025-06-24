package com.dinarastepina.decomposedictionary.domain.repository

import com.dinarastepina.decomposedictionary.domain.model.Text
import com.dinarastepina.decomposedictionary.domain.model.Topic

interface TextsRepository {
    suspend fun fetchAllTexts(): List<Text>
}